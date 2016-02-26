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
import java.util.List;

import com.google.common.util.concurrent.AbstractIdleService;
import eu.heronnet.module.kad.net.handler.RequestHandler;
import eu.heronnet.module.kad.net.handler.ResponseHandler;
import eu.heronnet.rpc.Messages;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class ServerImpl extends AbstractIdleService implements Server {
    private static final Logger logger = LoggerFactory.getLogger(ServerImpl.class);
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    private ServerBootstrap tcpBoostrap;
    private Bootstrap udpbootrap;

    @Inject
    private RequestHandler requestHandler;
    @Inject
    private ResponseHandler responseHandler;

    private final ChannelInitializer<SocketChannel> tcpChannelInitializer = new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            final ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("Logger", new LoggingHandler());
            pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
            pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());

            pipeline.addLast("protobufDecoder", new ProtobufDecoder(Messages.Request.getDefaultInstance()));
            pipeline.addLast("protobufEncoder", new ProtobufEncoder());

            pipeline.addLast("request handler", requestHandler);
        }
    };

    private final ChannelInitializer<DatagramChannel> udpChannelInitializer = new ChannelInitializer<DatagramChannel>() {
        @Override
        protected void initChannel(DatagramChannel ch) throws Exception {
            final ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new MessageToMessageDecoder<DatagramPacket>() {

                @Override
                protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> out) throws Exception {
                    final byte[] array;
                    final ByteBuf msg = packet.content();
                    final int length = msg.readableBytes();
                    if (msg.hasArray()) {
                        array = msg.array();
                    } else {
                        array = new byte[length];
                        msg.getBytes(msg.readerIndex(), array, 0, length);
                    }
                    out.add(Unpooled.wrappedBuffer(array));
                }
            });
            pipeline.addLast("protobuf Request decoder", new ProtobufDecoder(Messages.Request.getDefaultInstance()));
            pipeline.addLast("protobug Response decoder", new ProtobufDecoder(Messages.Response.getDefaultInstance()));
            pipeline.addLast("request handler", requestHandler);
            pipeline.addLast("response handler", responseHandler);
        }
    };



    @Override
    protected void startUp() throws Exception {
        logger.debug("calling startUp for ServerImpl instance={}", this);

        tcpBoostrap = new ServerBootstrap();
        udpbootrap = new Bootstrap();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        tcpBoostrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(6565)
                .childHandler(tcpChannelInitializer)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        tcpBoostrap.bind().sync();

        udpbootrap.group(workerGroup)
                .channel(NioDatagramChannel.class)
                .localAddress(6565)
                .handler(udpChannelInitializer)
                .option(ChannelOption.SO_BROADCAST, true);
        udpbootrap.bind().sync();

    }

    @Override
    protected void shutDown() throws Exception {
        logger.debug("Shutting down network server");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
