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

import java.net.*;
import java.util.Enumeration;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.AbstractIdleService;

import eu.heronnet.module.kad.model.Node;
import eu.heronnet.module.kad.model.RadixTree;
import eu.heronnet.module.kad.model.rpc.message.KadMessage;
import eu.heronnet.module.kad.model.rpc.message.PingRequest;
import eu.heronnet.module.kad.net.codec.KadMessageCodec;
import eu.heronnet.module.kad.net.handler.FindValueResponseHandler;
import eu.heronnet.module.kad.net.handler.PingResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

@Component
public class ClientImpl extends AbstractIdleService implements Client {

    private static final Logger logger = LoggerFactory.getLogger(ClientImpl.class);

    @Inject
    private RadixTree routingTable;

    private Bootstrap bootstrap;

    private EventLoopGroup workerGroup;

    private LoggingHandler loggingHandler = new LoggingHandler();

    @Inject
    private PingResponseHandler pingResponseHandler;

    @Inject
    private FindValueResponseHandler findValueResponseHandler;

    private final ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            final ChannelPipeline pipeline = ch.pipeline();
            pipeline.addFirst(loggingHandler);
            pipeline.addLast(new KadMessageCodec());
            pipeline.addLast(pingResponseHandler);
            pipeline.addLast(findValueResponseHandler);
        }
    };

    public ClientImpl() {
    }

    @Override
    protected void startUp() throws Exception {
        bootstrap = new Bootstrap();
        workerGroup = new NioEventLoopGroup();

        bootstrap.group(workerGroup).channel(NioSocketChannel.class).handler(channelInitializer);
    }

    @Override
    protected void shutDown() throws Exception {
        logger.debug("Shutting down network client");
        workerGroup.shutdownGracefully();
    }

    /**
     *
     * @param message to be posted
     */
    @Override
    public void send(KadMessage message) {
        final byte[] messageId = message.getMessageId();

        final List<Node> nodes = routingTable.find(messageId);
        for (Node node : nodes) {
            List<InetSocketAddress> addresses = node.getAddresses();
            for (InetSocketAddress address : addresses) {
                final ChannelFuture future;
                try {
                    future = bootstrap.connect(address.getAddress(), address.getPort()).sync();
                    final Channel channel = future.awaitUninterruptibly().channel();
                    ChannelFuture responseFuture = channel.writeAndFlush(message);
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
                }
            }
        }
    }

    @Override
    public void broadcast(PingRequest pingRequest) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                // we don't want to ping ourselves
                if (networkInterface.isLoopback()) return;
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress address = interfaceAddress.getAddress();
                    // site-local addresses are deprecated
                    if (address.isSiteLocalAddress()) return;
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast != null) {
                        final ChannelFuture future = bootstrap.connect(new InetSocketAddress(broadcast, 6565));
                        final Channel channel = future.awaitUninterruptibly().channel();
                        channel.writeAndFlush(broadcast);
                    }
                }
            }
        }
        catch (SocketException e) {
            logger.error(e.getMessage());
        }
    }
}
