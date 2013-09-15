package eu.heronnet.core.module.network.simple;

import com.google.inject.Inject;
import eu.heronnet.core.model.BinaryItem;
import eu.heronnet.core.module.network.dht.DHTService;
import eu.heronnet.core.module.network.simple.handlers.Ping;
import eu.heronnet.core.module.storage.Persistence;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;



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
public class NettyClientImpl extends DHTService {

    private final Logger logger = LoggerFactory.getLogger(NettyClientImpl.class);

    private List<InetSocketAddress> peers = new ArrayList<InetSocketAddress>();

    @Inject
    Persistence persistence;
    private Bootstrap bootstrap;
    private NioEventLoopGroup eventLoopGroup;


    @Override
    public UUID persist(final BinaryItem data) {

        for (InetSocketAddress peer : peers) {
            ChannelFuture cf = bootstrap.connect(peer);
            cf.addListener( new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture future) throws Exception {
                    logger.debug("persisting {}", data.getUUID());
                    if (future.isSuccess()) {
                        final Channel channel = future.channel();
                        channel.write(data);
                    }
                }
            });
        }
        return data.getUUID();
    }

    @Override
    public void ping() {

        peers.add(new InetSocketAddress("127.0.0.1", 5555));

        for (final InetSocketAddress peer : peers) {
            ChannelFuture cf = bootstrap.connect(peer);
            cf.addListener( new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture future) throws Exception {
                    logger.debug("Pinging peer: {}", peer.toString() );
                    if (future.isSuccess()) {
                        final Channel channel = future.channel();
                        channel.write("PING: " + new Date());
                    }
                }
            });
            try {
                cf.sync();
            } catch (InterruptedException e) {
                logger.debug(e.getMessage());
            }
        }
    }

    @Override
    public BinaryItem findByID(final UUID id) {
        return persistence.findByID(id);
    }

    @Override
    public void deleteByID(final UUID id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void startUp() throws Exception {

        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new Ping());
            }
        });
    }

    @Override
    protected void shutDown() throws Exception {
        eventLoopGroup.shutdownGracefully();
    }
}
