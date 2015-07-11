package eu.heronnet.module.kad.net.handler;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eu.heronnet.core.model.Document;
import eu.heronnet.module.kad.model.rpc.message.FindValueRequest;
import eu.heronnet.module.kad.model.rpc.message.FindValueResponse;
import eu.heronnet.module.kad.net.SelfNodeProvider;
import eu.heronnet.module.storage.Persistence;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author edoardocausarano
 */
@Component
@ChannelHandler.Sharable
public class FindValueRequestHandler extends SimpleChannelInboundHandler<FindValueRequest>{

    private static final Logger logger = LoggerFactory.getLogger(FindValueRequestHandler.class);

    @Inject
    Persistence persistence;
    @Inject
    SelfNodeProvider selfNodeProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FindValueRequest msg) throws Exception {
        logger.debug("Received request for value: {}", msg.getValue());

        List<Document> byHash = persistence.findByHash(Collections.singletonList(msg.getValue()));

        FindValueResponse response = new FindValueResponse();
        response.setDocuments(byHash);
        response.setOrigin(selfNodeProvider.getSelf());
        ctx.writeAndFlush(response);
    }
}
