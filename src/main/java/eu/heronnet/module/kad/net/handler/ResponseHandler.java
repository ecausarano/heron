package eu.heronnet.module.kad.net.handler;

import javax.inject.Inject;

import java.util.List;

import com.google.common.eventbus.EventBus;
import eu.heronnet.module.bus.command.UpdateResults;
import eu.heronnet.module.storage.util.HexUtil;
import eu.heronnet.rpc.Messages;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author edoardocausarano
 */
@Component
@ChannelHandler.Sharable
public class ResponseHandler extends SimpleChannelInboundHandler<Messages.Response> {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    @Inject
    private EventBus eventBus;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Messages.Response message) throws Exception {

        switch (message.getBodyCase()) {
            case FIND_VALUE_RESPONSE:
                Messages.FindValueResponse findValueResponse = message.getFindValueResponse();
                logger.debug("Received response id=[{}]", HexUtil.bytesToHex(findValueResponse.getMessageId().toByteArray()));
                List<Messages.Bundle> bundles = message.getFindValueResponse().getBundlesList();
                // TODO map Messages.Bundle to app domain Bundle
//                eventBus.post(new UpdateResults(bundles));
                break;
            default:
                logger.debug("Received unhandled response of type={}", message.getBodyCase());
        }




    }
}
