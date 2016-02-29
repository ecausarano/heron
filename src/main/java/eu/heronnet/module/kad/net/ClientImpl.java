/*
 * Copyright (C) 2014 edoardocausarano
 *
 * heron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * heron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with heron. If not, see http://www.gnu.org/licenses
 */

package eu.heronnet.module.kad.net;


import javax.inject.Inject;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.protobuf.ByteString;
import eu.heronnet.model.Bundle;
import eu.heronnet.module.kad.model.Node;
import eu.heronnet.module.kad.model.RoutingTable;
import eu.heronnet.module.kad.net.handler.ResponseHandler;
import eu.heronnet.module.storage.Persistence;
import eu.heronnet.rpc.Messages;
import eu.heronnet.rpc.Messages.Address;
import eu.heronnet.rpc.Messages.FindValueRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(value = "distributedStorage")
public class ClientImpl extends AbstractIdleService implements Persistence {

    private static final Logger logger = LoggerFactory.getLogger(ClientImpl.class);

    private Bootstrap tcpBoostrap;
    private Bootstrap udpBoostrap;
    private EventLoopGroup workerGroup;
    private LoggingHandler loggingHandler = new LoggingHandler();

    @Inject
    private RoutingTable<Node, byte[]> routingTable;
    @Inject
    private ResponseHandler responseHandler;
    @Inject
    private SelfNodeProvider selfNodeProvider;
    @Inject
    private IdGenerator idGenerator;

    private final ChannelInitializer<SocketChannel> tcpChannelInitializer = new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            final ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
            pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());

            pipeline.addLast("protobufResponseDecoder", new ProtobufDecoder(Messages.Response.getDefaultInstance()));
            pipeline.addLast("protobufEncoder", new ProtobufEncoder());

            pipeline.addLast(responseHandler);
        }
    };

    private final ChannelInitializer<NioDatagramChannel> udpChannelInitializer = new ChannelInitializer<NioDatagramChannel>() {
        @Override
        protected void initChannel(NioDatagramChannel ch) throws Exception {
        }
    };

    @Override
    protected void startUp() throws Exception {
        tcpBoostrap = new Bootstrap();
        udpBoostrap = new Bootstrap();
        workerGroup = new NioEventLoopGroup();

        tcpBoostrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(tcpChannelInitializer);
        udpBoostrap.group(workerGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(udpChannelInitializer);
    }

    @Override
    protected void shutDown() throws Exception {
        logger.debug("Shutting down network client");
        workerGroup.shutdownGracefully();
    }

    public void broadcast() {
        try {
            Messages.NetworkNode.Builder selfNodeBuilder = Messages.NetworkNode.newBuilder();
            Node self = selfNodeProvider.getSelf();
            self.getAddresses().forEach(address -> {
                Address.Builder addressBuilder = Address.newBuilder()
                        .setPort(6565)
                        .setIpAddress(ByteString.copyFrom(address));
                selfNodeBuilder.addAddresses(addressBuilder);
            });
            selfNodeBuilder.setId(ByteString.copyFrom(self.getId()));

            final Messages.PingRequest.Builder pingRequestBuilder = Messages.PingRequest.newBuilder();
            pingRequestBuilder.setMessageId(ByteString.copyFrom(idGenerator.getId()));
            pingRequestBuilder.setOrigin(selfNodeBuilder);

            final Messages.Request.Builder request = Messages.Request.newBuilder().setPingRequest(pingRequestBuilder);

            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            Collections.list(interfaces).forEach(networkInterface -> {
                try {
                    if (networkInterface.isLoopback()) return;
                    final List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
                    interfaceAddresses.forEach(interfaceAddress -> {
                        InetAddress broadcast = interfaceAddress.getBroadcast();
                        if (broadcast != null) {
                            final ChannelFuture future = udpBoostrap.bind(0);
                            future.addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) throws Exception {
                                    if (future.isSuccess()) {
                                        final Channel channel = future.channel();

                                        final ByteBuf requestBuffer = Unpooled.wrappedBuffer(request.build().toByteArray());
                                        final DatagramPacket datagramPacket = new DatagramPacket(
                                                requestBuffer,
                                                new InetSocketAddress(broadcast, 6565));
                                        channel.writeAndFlush(datagramPacket);
                                        channel.close();
                                        logger.debug("completed operation: {}", future.toString());
                                    } else {
                                        final Throwable cause = future.cause();
                                        logger.error("Error in channel bootstrap: {}", cause.getMessage());
                                    }
                                }
                            });
                        }
                    });
                } catch (SocketException e) {
                    logger.error("Some weird error has occurred, just logging: {}", e.getMessage());
                }
            });
        } catch (SocketException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void put(Bundle rawBundle) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void deleteBySubjectId(byte[] subjectId) {
        throw new RuntimeException("not implemented... never will");
    }

    @Override
    public List<Bundle> findByPredicate(List<? extends eu.heronnet.model.Node> fields) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public List<Bundle> findByHash(List<byte[]> searchKeys) {

        ArrayList<Node> targetNodes = new ArrayList<>();
        searchKeys.forEach(key -> targetNodes.addAll(routingTable.find(key)));

        try {
            // generateself node
            Node self = selfNodeProvider.getSelf();
            Messages.NetworkNode.Builder selfNodeBuilder = Messages.NetworkNode.newBuilder();
            self.getAddresses().forEach(address -> {
                Address.Builder addressBuilder = Address.newBuilder()
                        .setPort(6565)
                        .setIpAddress(ByteString.copyFrom(address));
                selfNodeBuilder.addAddresses(addressBuilder);
            });
            selfNodeBuilder.setId(ByteString.copyFrom(self.getId()));

            // build the wire request
            FindValueRequest.Builder findValueRequestBuilder = FindValueRequest.newBuilder();
            findValueRequestBuilder.setMessageId(ByteString.copyFrom(idGenerator.getId()));
            findValueRequestBuilder.setOrigin(selfNodeBuilder);
            findValueRequestBuilder.addAllValues(searchKeys.stream().map(ByteString::copyFrom).collect(Collectors.toList()));

            Messages.Request.Builder requestBuilder = Messages.Request.newBuilder().setFindValueRequest(findValueRequestBuilder);
            for (Node node : targetNodes) {
                List<byte[]> addresses = node.getAddresses();
                addresses.forEach(address -> {
                    final ChannelFuture future;
                    try {
                        future = tcpBoostrap.connect(InetAddress.getByAddress(address), 6565).sync();
                        final Channel channel = future.awaitUninterruptibly().channel();

                        ChannelFuture responseFuture = channel.writeAndFlush(requestBuilder.build());
                        responseFuture.addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                logger.debug("completed operation: {}", channelFuture.toString());
                            }
                        });
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
                    } catch (RuntimeException e) {
                        logger.error("Unresolved address {}", address);
                    } catch (UnknownHostException e) {
                        logger.error("Unkown host={}", e);
                    }
                });
            }
        } catch (SocketException e) {
            logger.error("Socket exception while building list of self nodes={}", e.getMessage());
        }

        return Collections.emptyList();
    }

    @Override
    public List<Bundle> getAll() {
        throw new RuntimeException("not implemented");
    }
}
