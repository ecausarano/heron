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
import eu.heronnet.kad.net.codec.KadMessageCodec;
import eu.heronnet.kad.net.handler.FindNodeRequestHandler;
import eu.heronnet.kad.net.handler.PingRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class ServerImpl extends AbstractIdleService {
    private static final Logger logger = LoggerFactory.getLogger(ServerImpl.class);
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;

    @Inject
    private KadMessageCodec kadMessageCodec;
    @Inject
    private PingRequestHandler pingRequestHandler;
    @Inject
    private FindNodeRequestHandler findNodeRequestHandler;

    @Override
    protected void startUp() throws Exception {
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(6565)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        final ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("Logger", new LoggingHandler());
                        pipeline.addLast("Kad message codec", kadMessageCodec);
                        pipeline.addLast("PING request handler", pingRequestHandler);
                        pipeline.addLast("FIND request handler", findNodeRequestHandler);
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            ChannelFuture future = bootstrap.bind().sync();
        } catch (InterruptedException e) {
            logger.error("An error has occurred", e);
        }
    }

    @Override
    protected void shutDown() throws Exception {
        logger.debug("Shutting down network server");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
