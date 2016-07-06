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


import com.google.common.util.concurrent.AbstractIdleService;
import com.google.protobuf.ByteString;
import eu.heronnet.model.*;
import eu.heronnet.model.vocabulary.DC;
import eu.heronnet.model.vocabulary.HRN;
import eu.heronnet.module.kad.model.Node;
import eu.heronnet.module.kad.model.RoutingTable;
import eu.heronnet.module.kad.net.handler.ResponseHandler;
import eu.heronnet.module.storage.Persistence;
import eu.heronnet.rpc.Messages;
import eu.heronnet.rpc.Messages.Address;
import eu.heronnet.rpc.Messages.FindValueRequest;
import eu.heronnet.rpc.Messages.NetworkNode.Builder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

@Component(value = "distributedStorage")
public class ClientImpl extends AbstractIdleService implements Persistence {

    private static final Logger logger = LoggerFactory.getLogger(ClientImpl.class);

    private Bootstrap tcpBoostrap;
    private Bootstrap udpBoostrap;
    private EventLoopGroup workerGroup;

    @Inject
    private RoutingTable<Node, byte[]> routingTable;
    @Inject
    private SelfNodeProvider selfNodeProvider;
    @Inject
    private IdGenerator idGenerator;

    private class HeronResponseChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            logger.debug("Initialized channel, id={}", ch.toString());
            final ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
            pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());

            pipeline.addLast("protobufResponseDecoder", new ProtobufDecoder(Messages.Response.getDefaultInstance()));
            pipeline.addLast("protobufEncoder", new ProtobufEncoder());
        }
    }

    private final ChannelInitializer<NioDatagramChannel> udpChannelInitializer = new ChannelInitializer<NioDatagramChannel>() {
        @Override
        protected void initChannel(NioDatagramChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
        }
    };

    @Override
    protected void startUp() throws Exception {
        tcpBoostrap = new Bootstrap();
        udpBoostrap = new Bootstrap();
        workerGroup = new NioEventLoopGroup();

        tcpBoostrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new HeronResponseChannelInitializer());

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

    void broadcast() {
        try {
            final Messages.PingRequest pingRequest = Messages.PingRequest.newBuilder()
                    .setPayload(ByteString.copyFrom(idGenerator.getId())).build();

            final Messages.Request request = Messages.Request.newBuilder()
                    .setMessageId(ByteString.copyFrom(idGenerator.getId()))
                    .setOrigin(originBuilder())
                    .setPingRequest(pingRequest).build();

            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            Collections.list(interfaces).stream()
                    .filter(networkInterface -> {
                        try {
                            return !networkInterface.isLoopback() && networkInterface.isUp();
                        } catch (SocketException e) {
                            return false;
                        }})
                    .forEach(networkInterface -> {
                        final List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
                        interfaceAddresses.forEach(interfaceAddress -> broadcastOnInterface(interfaceAddress, request));
                    });
        } catch (SocketException se) {
            logger.error(se.getMessage());
        }
    }

    private void broadcastOnInterface(InterfaceAddress interfaceAddress, Messages.Request request) {
        InetAddress broadcast = interfaceAddress.getBroadcast();
        if (broadcast != null) {
            ByteString messageId = request.getMessageId();
            udpBoostrap.handler(new ResponseHandler(messageId.toByteArray()));
            udpBoostrap.bind(0).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        final Channel channel = future.channel();

                        final ByteBuf requestBuffer = Unpooled.wrappedBuffer(request.toByteArray());
                        final DatagramPacket datagramPacket = new DatagramPacket(
                                requestBuffer,
                                new InetSocketAddress(broadcast, selfNodeProvider.getSelf().getPort()));
                        channel.writeAndFlush(datagramPacket);
                        channel.close();
                        logger.debug("completed operation: {}", future.toString());
                    } else {
                        logger.error("Error in channel bootstrap: {}", future.cause().getMessage());
                    }
                }
            });
        }
    }

    private Builder originBuilder() throws RuntimeException {
        try {
            Builder selfNodeBuilder = Messages.NetworkNode.newBuilder();
        Node self = selfNodeProvider.getSelf();
        self.getAddresses().forEach(address -> {
            Address.Builder addressBuilder = Address.newBuilder()
                    .setPort(self.getPort())
                    .setIpAddress(ByteString.copyFrom(address));
            selfNodeBuilder.addAddresses(addressBuilder);
        });
        selfNodeBuilder.setId(ByteString.copyFrom(self.getId()));
        return selfNodeBuilder;
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * TODO call findByHash with bundle's ID to find nodes in closest bucket to publish to.
     *
     *
     * @param bundle
     */
    @Override
    public void put(Bundle bundle) {

        final Messages.Bundle.Builder bundleBuilder = Messages.Bundle.newBuilder();
        bundleBuilder.setSubject(ByteString.copyFrom(bundle.getSubject().getNodeId()));

        // TODO - type switch on getNodeType, in a separate class... IOW un-hack
        bundle.getStatements().forEach(statement ->  {
            final IRI predicate = statement.getPredicate();
            final Messages.Statement.Builder wireStatementBuilder = Messages.Statement.newBuilder();
            wireStatementBuilder.setPredicate(predicate.toString());
            if (HRN.BINARY.equals(predicate)) {
                final BinaryDataNode binaryDataNode = (BinaryDataNode) statement.getObject();
                wireStatementBuilder.setBinaryValue(ByteString.copyFrom(binaryDataNode.getData()));
                bundleBuilder.addStatements(wireStatementBuilder);
            } else if (DC.DATE.equals(predicate)) {
                final DateNode dateNode = (DateNode) statement.getObject();
                wireStatementBuilder.setDateValue(dateNode.getData().getTime());
                bundleBuilder.addStatements(wireStatementBuilder);
            } else { // default to string
                final StringNode stringNode = (StringNode) statement.getObject();
                wireStatementBuilder.setStringValue(stringNode.getData());
                bundleBuilder.addStatements(wireStatementBuilder);
            }
        });

        final Messages.StoreValueRequest.Builder storeValueRequestBuilder = Messages.StoreValueRequest.newBuilder()
                .addBundles(bundleBuilder);
        final List<Node> nodes = routingTable.find(bundle.getSubject().getNodeId());

        final Messages.Request.Builder requestBuilder = Messages.Request.newBuilder()
                .setMessageId(ByteString.copyFrom(idGenerator.getId()))
                .setOrigin(originBuilder())
                .setStoreValueRequest(storeValueRequestBuilder);
        sendAllNodes(nodes, requestBuilder);
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

        FindValueRequest.Builder findValueRequestBuilder = FindValueRequest.newBuilder();
        findValueRequestBuilder.addAllValues(searchKeys.stream().map(ByteString::copyFrom).collect(Collectors.toList()));

        byte[] requestId = idGenerator.getId();

        final Messages.Request.Builder requestBuilder = Messages.Request.newBuilder()
                .setMessageId(ByteString.copyFrom(requestId))
                .setOrigin(originBuilder())
                .setFindValueRequest(findValueRequestBuilder);

        final List<Node> targets = searchKeys.stream().flatMap(key -> routingTable.find(key).stream()).collect(Collectors.toList());
        sendAllNodes(targets, requestBuilder);

        return Collections.emptyList();
    }

    private void sendAllNodes(List<Node> nodes, Messages.Request.Builder requestBuilder) {
        try {
            Node self = selfNodeProvider.getSelf();
            Builder selfNodeBuilder = Messages.NetworkNode.newBuilder();
            self.getAddresses().forEach(address -> {
                Address.Builder addressBuilder = Address.newBuilder()
                        .setPort(self.getPort())
                        .setIpAddress(ByteString.copyFrom(address));
                selfNodeBuilder.addAddresses(addressBuilder);
            });
            selfNodeBuilder.setId(ByteString.copyFrom(self.getId()));
            requestBuilder.setOrigin(selfNodeBuilder);

            for (Node node : nodes) {
                List<byte[]> addresses = node.getAddresses();
                addresses.forEach(address -> {
                    try {
                        ByteString messageId = requestBuilder.getMessageId();
                        tcpBoostrap.handler(new ResponseHandler(messageId.toByteArray()));
                        ChannelFuture connectFuture = tcpBoostrap.connect(InetAddress.getByAddress(address), node.getPort()).sync();
                        connectFuture.addListener(future -> {
                            Messages.Request request = requestBuilder.build();
                            connectFuture.channel().writeAndFlush(request);
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
    }

    @Override
    public List<Bundle> getAll() {
        throw new RuntimeException("not implemented");
    }
}
