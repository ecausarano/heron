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

import com.google.common.util.concurrent.AbstractIdleService;
import eu.heronnet.kad.model.rpc.message.KadMessage;
import eu.heronnet.kad.net.codec.KadMessageCodec;
import eu.heronnet.kad.net.handler.PingResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

public class ClientImpl extends AbstractIdleService implements Client {

    private Bootstrap boostrap;
    private EventLoopGroup workerGroup;

    @Override
    protected void startUp() throws Exception {
        boostrap = new Bootstrap();
        workerGroup = new NioEventLoopGroup();

        boostrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
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
        workerGroup.shutdownGracefully();
    }

    @Override
    public void send(KadMessage message, InetSocketAddress address) {
        final ChannelFuture future = boostrap.connect(address);
        final Channel channel = future.awaitUninterruptibly().channel();
        final ChannelFuture write = channel.writeAndFlush(message);
    }

}