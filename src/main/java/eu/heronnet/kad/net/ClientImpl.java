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

package eu.heronnet.kad.net;

import java.net.InetSocketAddress;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractIdleService;

import eu.heronnet.kad.model.Node;
import eu.heronnet.kad.model.RadixTree;
import eu.heronnet.kad.model.rpc.message.KadMessage;
import eu.heronnet.kad.net.codec.KadMessageCodec;
import eu.heronnet.kad.net.handler.PingResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

@Singleton
public class ClientImpl extends AbstractIdleService implements Client {

    private static final Logger logger = LoggerFactory.getLogger(ClientImpl.class);
    @Inject
    RadixTree routingTable;
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;

    public ClientImpl() {
        logger.debug("ClientImpl ctor id={}", this);
    }

    @Override
    protected void startUp() throws Exception {
        bootstrap = new Bootstrap();
        workerGroup = new NioEventLoopGroup();

        bootstrap.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                final ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LoggingHandler());
                pipeline.addLast(new KadMessageCodec());
                pipeline.addLast(new PingResponseHandler());
            }
        });
    }

    @Override
    protected void shutDown() throws Exception {
        logger.debug("Shutting down network client");
        workerGroup.shutdownGracefully();
    }

    @Override
    public void send(KadMessage message) {
        final byte[] messageId = message.getMessageId();

        final List<Node> nodes = routingTable.find(messageId);
        for (Node node : nodes) {
            InetSocketAddress address = node.getAddress();
            final ChannelFuture future = bootstrap.connect(address);
            final Channel channel = future.awaitUninterruptibly().channel();
            channel.writeAndFlush(message);
        }
    }

}
