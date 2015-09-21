package eu.heronnet.module.kad.net.handler;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import com.google.protobuf.ByteString;
import eu.heronnet.model.Bundle;
import eu.heronnet.model.StringNode;
import eu.heronnet.module.kad.model.Node;
import eu.heronnet.module.kad.model.RadixTree;
import eu.heronnet.module.kad.net.IdGenerator;
import eu.heronnet.module.kad.net.SelfNodeProvider;
import eu.heronnet.module.storage.Persistence;
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
public class RequestHandler extends SimpleChannelInboundHandler<Messages.Request>{

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    @Inject
    Persistence persistence;
    @Inject
    SelfNodeProvider selfNodeProvider;
    @Inject
    IdGenerator idGenerator;
    @Inject
    RadixTree network;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Messages.Request msg) throws Exception {
        switch (msg.getBodyCase()) {
            case FIND_VALUE_REQUEST:
                findValueRequest(ctx, msg.getFindValueRequest());
                break;
            case FIND_NODE_REQUEST:
                findNodeRequest(ctx, msg.getFindNodeRequest());
                break;
            default:
                LOGGER.debug("received unhandled request={}", msg.getDescriptorForType().getFullName());
        }
    }


    private void findValueRequest(ChannelHandlerContext ctx, Messages.FindValueRequest request) throws Exception{
        List<ByteString> valuesList = request.getValuesList();

        if (LOGGER.isDebugEnabled()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (ByteString bytes : valuesList) {
                stringBuilder.append("[");
                stringBuilder.append(HexUtil.bytesToHex(bytes.toByteArray()));
                stringBuilder.append("]");
            }
            LOGGER.debug("Received request for values={}", stringBuilder.toString());
        }

        List<byte[]> requestAsBytes = valuesList.stream().map(ByteString::toByteArray).collect(Collectors.toList());
        List<Bundle> byHash = persistence.findByHash(requestAsBytes);

        Messages.FindValueResponse.Builder responseBuilder = Messages.FindValueResponse.newBuilder();
        responseBuilder.setMessageId(ByteString.copyFrom(idGenerator.getId()));
        responseBuilder.setOrigin(Messages.NetworkNode.newBuilder().setId(ByteString.copyFrom(selfNodeProvider.getSelf().getId())));

        byHash.forEach(bundle -> {
            Messages.Bundle.Builder bundleBuilder = Messages.Bundle.newBuilder();
            bundleBuilder.setSubject(ByteString.copyFrom(bundle.getSubject().getNodeId()));
            bundle.getStatements().forEach(statement -> {
                Messages.Statement.Builder statementBuilder = Messages.Statement.newBuilder();
                statementBuilder.setPredicate(statement.getPredicate().getData());
                eu.heronnet.model.Node statementObject = statement.getObject();
                switch (statementObject.getNodeType()) {
                    case STRING:
                        StringNode stringNode = (StringNode) statementObject;
                        statementBuilder.setStringValue(stringNode.getData());
                        break;
                    default:
                        LOGGER.debug("ignored unknown node type={}", statementObject.getNodeType());
                }

                bundleBuilder.addStatements(statementBuilder.build());
            });

            responseBuilder.addBundles(bundleBuilder.build());
        });

        Messages.Response.Builder messageBuilder = Messages.Response.newBuilder().setFindValueResponse(responseBuilder);
        ctx.writeAndFlush(messageBuilder.build());
    }

    private void findNodeRequest(ChannelHandlerContext ctx, Messages.FindNodeRequest request) {
        LOGGER.debug("handling incoming request {}", request.getClass().toString());

        final byte[] nodeId = request.getNodeId().toByteArray();
        final List<Node> nodes = network.find(nodeId);

        Messages.FindNodeResponse.Builder responseBuilder = Messages.FindNodeResponse.newBuilder();
//        responseBuilder.setOrigin(selfNodeProvider.getSelf())

//        final FindNodeResponse response = new FindNodeResponse(selfNodeProvider.getSelf(), nodes);
//        ctx.writeAndFlush(response);
    }
}
