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

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eu.heronnet.module.kad.model.Node;
import eu.heronnet.module.kad.model.RadixTree;
import eu.heronnet.module.kad.model.rpc.message.FindNodeRequest;
import eu.heronnet.module.kad.model.rpc.message.FindNodeResponse;
import eu.heronnet.module.kad.net.SelfNodeProvider;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Component
@ChannelHandler.Sharable
public class FindNodeRequestHandler extends SimpleChannelInboundHandler<FindNodeRequest> {

    private static final Logger logger = LoggerFactory.getLogger(FindNodeRequestHandler.class);

    @Inject
    RadixTree network;

    @Inject
    SelfNodeProvider selfNodeProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FindNodeRequest msg) throws Exception {
        logger.debug("handling incoming request {}", msg.getClass().toString());

        final byte[] nodeId = msg.getNodeId();
        final List<Node> nodes = network.find(nodeId);

        final FindNodeResponse response = new FindNodeResponse();
        response.setFoundNodes(nodes);
        response.setOrigin(selfNodeProvider.getSelf());

        ctx.writeAndFlush(response);
    }
}
