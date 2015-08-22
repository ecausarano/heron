package eu.heronnet.module.kad.net.handler;

import eu.heronnet.model.Bundle;
import eu.heronnet.module.kad.model.rpc.message.FindValueRequest;
import eu.heronnet.module.kad.model.rpc.message.FindValueResponse;
import eu.heronnet.module.kad.net.SelfNodeProvider;
import eu.heronnet.module.storage.Persistence;
import eu.heronnet.module.storage.util.HexUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

/**
 * @author edoardocausarano
 */
@Component
@ChannelHandler.Sharable
public class FindValueRequestHandler extends SimpleChannelInboundHandler<FindValueRequest>{

    private static final Logger LOGGER = LoggerFactory.getLogger(FindValueRequestHandler.class);

    @Inject
    Persistence persistence;
    @Inject
    SelfNodeProvider selfNodeProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FindValueRequest msg) throws Exception {
        List<byte[]> request = msg.getValue();
        if (LOGGER.isDebugEnabled()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (byte[] bytes : request) {
                stringBuilder.append("[");
                stringBuilder.append(HexUtil.bytesToHex(bytes));
                stringBuilder.append("]");
            }
            LOGGER.debug("Received request for values={}", stringBuilder.toString());
        }

        List<Bundle> byHash = persistence.findByHash(request);

        FindValueResponse response = new FindValueResponse();
        response.setBundles(byHash);
        response.setOrigin(selfNodeProvider.getSelf());
        ctx.writeAndFlush(response);
    }
}
