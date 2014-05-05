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

package eu.heronnet.kad.net.handler;

import eu.heronnet.kad.model.rpc.message.PingResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingResponseHandler extends SimpleChannelInboundHandler<PingResponse> {

    private static final Logger logger = LoggerFactory.getLogger(PingResponseHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingResponse msg) throws Exception {
        logger.debug("received PING response {} from {}", msg.getMessageId(), msg.getOrigin().getId());
    }
}
