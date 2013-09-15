package eu.heronnet.core.module.network.simple;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import eu.heronnet.core.module.network.simple.handlers.Ping;
import eu.heronnet.core.module.storage.Persistence;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This file is part of heron Copyright (C) 2013-2013 edoardocausarano
 * <p/>
 * heron is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * <p/>
 * heron is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with Foobar.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
public class NettyServerImpl extends AbstractIdleService {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerImpl.class);

    @Inject
    Persistence persistence;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Override
    protected void startUp() {
        logger.debug("Starting up Server");
        ServerBootstrap bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            final ChannelPipeline pipeline = socketChannel.pipeline();
//                            socketChannel.pipeline().addLast(persistence.getHandler())
                            pipeline.addLast(new Ping());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            final ChannelFuture sync = bootstrap.bind(5555).sync();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    protected void shutDown() throws Exception {
        logger.debug("Shutting down Server");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();

    }
}
