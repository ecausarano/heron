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

package eu.heronnet.module.kad.net.codec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.heronnet.module.kad.model.rpc.message.KadMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

public class KadMessageCodec extends ByteToMessageCodec<KadMessage> {

    private static final Logger logger = LoggerFactory.getLogger(KadMessageCodec.class);

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, KadMessage msg, ByteBuf out) throws Exception {
        logger.debug("encoding KadMessage: {}", msg.toString());
        final ByteBufOutputStream outputStream = new ByteBufOutputStream(out);
        objectMapper.writeValue(outputStream, msg);
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        logger.debug("decoding KadMessage");
        final ByteBufInputStream inputStream = new ByteBufInputStream(in);
        try {
            final KadMessage message = objectMapper.readValue(inputStream, KadMessage.class);
            out.add(message);
        } catch (JsonParseException e) {
            logger.debug("Unable to decode incoming message");
        }
    }

}
