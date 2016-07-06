package eu.heronnet.module.kad.net.handler;

import com.google.common.eventbus.EventBus;
import com.google.protobuf.ByteString;
import eu.heronnet.model.*;
import eu.heronnet.module.bus.command.UpdateResults;
import eu.heronnet.module.kad.model.Node;
import eu.heronnet.module.storage.util.HexUtil;
import eu.heronnet.rpc.Messages;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author edoardocausarano
 */
public class ResponseHandler extends SimpleChannelInboundHandler<Messages.Response> {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final byte[] correlationId;

    public ResponseHandler(byte[] correlationId) {
        this.correlationId = correlationId;
    }

    @Inject
    @Qualifier(value = "mainBus")
    private EventBus mainBus;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Messages.Response message) throws Exception {
        ByteString requestId = message.getRequestId();
        logger.debug("received response for requestId={}", requestId);
        if (requestId.toByteArray().equals(correlationId)) {
            switch (message.getBodyCase()) {
                case FIND_VALUE_RESPONSE:
                    findValueResponseHandler(message);
                    break;
                case PING_RESPONSE:
                    pingResponse(message);
                    break;
                default:
                    logger.debug("Received unhandled response of type={}", message.getBodyCase());
            }
        } else {
            logger.warn("Received alien response for requestId={}", correlationId);
        }
    }

    private void findValueResponseHandler(Messages.Response message) {
        logger.debug("Received response id=[{}]", HexUtil.bytesToHex(message.getMessageId().toByteArray()));
        List<Messages.Bundle> wireBundles = message.getFindValueResponse().getBundlesList();

        ArrayList<Bundle> domainBundles = new ArrayList<>(wireBundles.size());
        wireBundles.forEach(bundle -> {
            BundleBuilder bundleBuilder = new BundleBuilder();

            bundleBuilder.withSubject(new IdentifierNode(bundle.getSubject().toByteArray()));

            List<Messages.Statement> statementsList = bundle.getStatementsList();
            statementsList.forEach(statement -> {
                eu.heronnet.model.Node object = null;
                // TODO - type mapping
                if (statement.getStringValue() != null) {
                    object = StringNodeBuilder.withString(statement.getStringValue());
                }
                bundleBuilder.withStatement(new Statement(IRIBuilder.withString(statement.getPredicate()), object));
            });
            domainBundles.add(bundleBuilder.build());
        });
        mainBus.post(new UpdateResults(domainBundles));
    }

    private void pingResponse(Messages.Response message) {
        Messages.NetworkNode origin = message.getOrigin();

        byte[] originId = origin.getId().toByteArray();
        List<byte[]> addresses = origin.getAddressesList().stream()
                .map(address -> address.getIpAddress().toByteArray()).collect(Collectors.toList());

        // check ping request creation, add RTT
        final Node node = new Node(originId, addresses);

        // add node to bucket holder
        logger.debug("Received ping response from node id={}", HexUtil.bytesToHex(node.getId()));
    }
}
