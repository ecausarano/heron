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

package eu.heronnet.module.kad.net.handler;

import eu.heronnet.module.kad.model.rpc.message.PingRequest;
import eu.heronnet.module.kad.model.rpc.message.PingResponse;
import eu.heronnet.module.kad.net.SelfNodeProvider;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@ChannelHandler.Sharable
public class PingRequestHandler extends SimpleChannelInboundHandler<PingRequest> {

    private static final Logger logger = LoggerFactory.getLogger(PingRequestHandler.class);

    @Inject
    SelfNodeProvider selfNodeProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingRequest msg) throws Exception {
        logger.debug("handling incoming request {}", msg.getClass().toString());
        final byte[] messageId = msg.getMessageId();
        final PingResponse response = new PingResponse();

        response.setOrigin(selfNodeProvider.getSelf());
        response.setResponse(messageId);

        ChannelFuture future = ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception while handling incoming request", cause);
    }
}
