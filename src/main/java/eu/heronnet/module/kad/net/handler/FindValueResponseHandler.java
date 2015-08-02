package eu.heronnet.module.kad.net.handler;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;

import eu.heronnet.module.bus.command.UpdateResults;
import eu.heronnet.module.kad.model.rpc.message.FindValueResponse;
import eu.heronnet.module.storage.util.HexUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author edoardocausarano
 */
@Component
@ChannelHandler.Sharable
public class FindValueResponseHandler extends SimpleChannelInboundHandler<FindValueResponse> {

    private static final Logger logger = LoggerFactory.getLogger(FindValueRequestHandler.class);

    @Inject
    private EventBus eventBus;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FindValueResponse findValueResponse) throws Exception {
        logger.debug("Received response id=[{}]", HexUtil.bytesToHex(findValueResponse.getMessageId()));
        eventBus.post(new UpdateResults(findValueResponse.getBundles()));
    }
}
