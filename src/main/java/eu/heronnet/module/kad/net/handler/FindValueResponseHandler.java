package eu.heronnet.module.kad.net.handler;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;

import eu.heronnet.core.model.Document;
import eu.heronnet.module.bus.command.UpdateResults;
import eu.heronnet.module.kad.model.Node;
import eu.heronnet.module.kad.model.rpc.message.FindValueResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author edoardocausarano
 */

@Component
@ChannelHandler.Sharable
public class FindValueResponseHandler extends SimpleChannelInboundHandler<FindValueResponse> {

    private static final Logger logger = LoggerFactory.getLogger(FindValueResponseHandler.class);

    @Inject
    @Named("self")
    Node self;

    @Inject
    EventBus eventBus;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FindValueResponse msg) throws Exception {
        List<Document> documents = mapper.readValue(msg.getPayload(), new TypeReference<List<Document>>() {
        });
        UpdateResults updateResults = new UpdateResults(documents);
        eventBus.post(updateResults);
    }
}
