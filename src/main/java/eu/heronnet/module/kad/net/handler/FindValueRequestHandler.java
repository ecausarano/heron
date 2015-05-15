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

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.heronnet.core.model.Document;
import eu.heronnet.module.kad.model.Node;
import eu.heronnet.module.kad.model.rpc.message.FindValueRequest;
import eu.heronnet.module.kad.model.rpc.message.FindValueResponse;
import eu.heronnet.module.kad.net.Client;
import eu.heronnet.module.storage.Persistence;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Component
@ChannelHandler.Sharable
public class FindValueRequestHandler extends SimpleChannelInboundHandler<FindValueRequest> {

    private static final Logger logger = LoggerFactory.getLogger(FindValueRequestHandler.class);

    @Inject
    @Named("self")
    Node self;

    @Inject
    Client client;

    @Inject
    Persistence persistence;

    private ObjectMapper mapper = new ObjectMapper();

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FindValueRequest msg) throws Exception {

        String key = new String(msg.getValue(), "utf-8");
        logger.debug("findValue requestId={}, value={}", byteArrayToHex(msg.getMessageId()), key);
        List<Document> documents = persistence.findByStringKey(Collections.singletonList(key));

        FindValueResponse response = new FindValueResponse();

        response.setPayload(mapper.writeValueAsBytes(documents));

        client.send(response);
    }

}
