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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eu.heronnet.module.kad.model.rpc.message.PingResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Component
@ChannelHandler.Sharable
public class PingResponseHandler extends SimpleChannelInboundHandler<PingResponse> {

    private static final Logger logger = LoggerFactory.getLogger(PingResponseHandler.class);

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingResponse msg) throws Exception {

        byte[] messageId = msg.getMessageId();
        byte[] originId = msg.getOrigin().getId();
        logger.debug("received PING response {} from {}", byteArrayToHex(messageId), byteArrayToHex(originId));
    }

}
