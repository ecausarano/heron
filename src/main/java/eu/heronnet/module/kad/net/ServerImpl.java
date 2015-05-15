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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.AbstractIdleService;

import eu.heronnet.module.kad.net.codec.KadMessageCodec;
import eu.heronnet.module.kad.net.handler.FindValueRequestHandler;
import eu.heronnet.module.kad.net.handler.FindValueResponseHandler;
import eu.heronnet.module.kad.net.handler.PingRequestHandler;
import eu.heronnet.module.kad.net.handler.StoreValueRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

@Component
public class ServerImpl extends AbstractIdleService implements Server {
    private static final Logger logger = LoggerFactory.getLogger(ServerImpl.class);
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;

    @Inject
    private KadMessageCodec kadMessageCodec;
    @Inject
    private PingRequestHandler pingRequestHandler;
    @Inject
    private StoreValueRequestHandler storeValueRequestHandler;
    @Inject
    private FindValueRequestHandler findValueRequestHandler;
    @Inject
    private FindValueResponseHandler findValueResponseHandler;

    @Override
    protected void startUp() throws Exception {
        logger.debug("calling startUp for ServerImpl instance={}", this);
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).localAddress(6565).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                final ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("Logger", new LoggingHandler());
                pipeline.addLast("Kad message codec", kadMessageCodec);
                pipeline.addLast("PING request handler", pingRequestHandler);
                pipeline.addLast("FIND request handler", findValueRequestHandler);
                pipeline.addLast("FIND response handler", findValueResponseHandler);
                pipeline.addLast("STORE request handle", storeValueRequestHandler);
            }
        }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            bootstrap.bind().sync();
        }
        catch (InterruptedException e) {
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
