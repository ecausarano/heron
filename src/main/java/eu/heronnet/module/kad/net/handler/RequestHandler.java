package eu.heronnet.module.kad.net.handler;

import javax.inject.Inject;
import java.net.SocketException;
import java.util.List;
import java.util.stream.Collectors;

import com.google.protobuf.ByteString;
import eu.heronnet.model.BinaryDataNode;
import eu.heronnet.model.Bundle;
import eu.heronnet.model.BundleBuilder;
import eu.heronnet.model.DateNode;
import eu.heronnet.model.DateNodeBuilder;
import eu.heronnet.model.IRI;
import eu.heronnet.model.IRIBuilder;
import eu.heronnet.model.IdentifierNode;
import eu.heronnet.model.Statement;
import eu.heronnet.model.StringNode;
import eu.heronnet.model.StringNodeBuilder;
import eu.heronnet.model.vocabulary.DC;
import eu.heronnet.model.vocabulary.HRN;
import eu.heronnet.module.kad.model.Node;
import eu.heronnet.module.kad.model.RoutingTable;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author edoardocausarano
 */
@Component
@ChannelHandler.Sharable
public class RequestHandler extends SimpleChannelInboundHandler<Messages.Request>{

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    @Inject
    @Qualifier(value = "localStorage")
    Persistence localStorage;
    @Inject
    SelfNodeProvider selfNodeProvider;
    @Inject
    IdGenerator idGenerator;
    @Inject
    RoutingTable<Node, byte[]> routingTable;


    @Override
    protected void channelRead0(ChannelHandlerContext context, Messages.Request message) throws Exception {
        final Messages.NetworkNode origin = message.getOrigin();
        switch (message.getBodyCase()) {
            case FIND_VALUE_REQUEST:
                findValueRequest(context, message.getFindValueRequest());
                break;
            case FIND_NODE_REQUEST:
                findNodeRequest(context, message.getFindNodeRequest());
                break;
            case PING_REQUEST:
                pingRequest(context, message.getPingRequest(), origin);
                break;
            case STORE_VALUE_REQUEST:
                storeValueRequest(context, message.getStoreValueRequest());
            default:
                LOGGER.debug("received unhandledrequest={}", message.getDescriptorForType().getFullName());
        }
    }

    private void storeValueRequest(ChannelHandlerContext channelHandlerContext, Messages.StoreValueRequest storeValueRequest) {
        final List<Messages.Bundle> bundles = storeValueRequest.getBundlesList();
        bundles.forEach(bundle -> {
            final ByteString subject = bundle.getSubject();
            final BundleBuilder bundleBuilder = new BundleBuilder();
            bundleBuilder.withSubject(new IdentifierNode(subject.toByteArray()));
            bundle.getStatementsList().forEach(wireStatement -> {
                final IRI predicate = IRIBuilder.withString(wireStatement.getPredicate());
                if (HRN.BINARY.equals(predicate)) {
                    final ByteString binaryValue = wireStatement.getBinaryValue();
                    bundleBuilder.withStatement(new Statement(
                            predicate,
                            new BinaryDataNode(subject.toByteArray(), binaryValue.toByteArray())));
                } else if (DC.DATE.equals(predicate)) {
                    final DateNode dateNode = DateNodeBuilder.withDate(wireStatement.getDateValue());
                    bundleBuilder.withStatement(new Statement(predicate, dateNode));
                } else {
                    final String stringValue = wireStatement.getStringValue();
                    bundleBuilder.withStatement(new Statement(predicate, StringNodeBuilder.withString(stringValue)));
                }
            });
            localStorage.put(bundleBuilder.build());
        });
    }

    private void pingRequest(ChannelHandlerContext context, Messages.PingRequest request, Messages.NetworkNode origin) {
        try {

            final List<Messages.Address> addressesList = origin.getAddressesList();
            final List<byte[]> addresses = addressesList.stream().map(address -> address.getIpAddress().toByteArray()).collect(Collectors.toList());
            final Node node = new Node(origin.getId().toByteArray(), addresses);
            routingTable.insert(node);

            final Messages.PingResponse.Builder pingResponse = Messages.PingResponse.newBuilder();
            pingResponse.setMessageId(ByteString.copyFrom(idGenerator.getId()));
            pingResponse.setResponse(ByteString.copyFrom(request.getMessageId().toByteArray()));

            final Messages.Response.Builder responseBuilder = Messages.Response.newBuilder()
                    .setOrigin(createSelfNetworkNodeBuilder())
                    .setPingResponse(pingResponse);

            context.writeAndFlush(responseBuilder.build());
        } catch (SocketException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void findValueRequest(ChannelHandlerContext context, Messages.FindValueRequest request) throws Exception{
        List<ByteString> valuesList = request.getValuesList();

        if (LOGGER.isDebugEnabled()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (ByteString bytes : valuesList) {
                stringBuilder.append("[");
                stringBuilder.append(HexUtil.bytesToHex(bytes.toByteArray()));
                stringBuilder.append("]");
            }
            LOGGER.debug("Received message for values={}", stringBuilder.toString());
        }

        List<byte[]> requestAsBytes = valuesList.stream().map(ByteString::toByteArray).collect(Collectors.toList());
        List<Bundle> byHash = localStorage.findByHash(requestAsBytes);

        Messages.FindValueResponse.Builder responseBuilder = Messages.FindValueResponse.newBuilder();
        responseBuilder.setMessageId(ByteString.copyFrom(idGenerator.getId()));

        byHash.forEach(bundle -> {
            Messages.Bundle.Builder bundleBuilder = Messages.Bundle.newBuilder();
            bundleBuilder.setSubject(ByteString.copyFrom(bundle.getSubject().getNodeId()));
            bundle.getStatements().forEach(statement -> {
                Messages.Statement.Builder statementBuilder = Messages.Statement.newBuilder();
                statementBuilder.setPredicate(statement.getPredicate().getData());
                eu.heronnet.model.Node statementObject = statement.getObject();
                // TODO - move out to an external adapter
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

        Messages.Response.Builder messageBuilder = Messages.Response.newBuilder()
                .setOrigin(createSelfNetworkNodeBuilder())
                .setFindValueResponse(responseBuilder);
        context.writeAndFlush(messageBuilder.build());
    }

    private void findNodeRequest(ChannelHandlerContext context, Messages.FindNodeRequest request) {
        try {
            Messages.FindNodeResponse.Builder findNodeResponseBuilder = Messages.FindNodeResponse.newBuilder();

            final byte[] nodeId = request.getNodeId().toByteArray();
            final List<Node> nodes = routingTable.find(nodeId);

            final List<Messages.NetworkNode> networkNodesList = nodes.stream().map(node -> {
                final Messages.NetworkNode.Builder networkNodeBuilder = Messages.NetworkNode.newBuilder();
                networkNodeBuilder.setId(ByteString.copyFrom(node.getId()));
                networkNodeBuilder.setLastSeen(node.getLastSeen().getTime());
                networkNodeBuilder.setRtt(node.getRTT());
                final List<Messages.Address> addressBuilders = mapModelAddressToWireAddress(node.getAddresses());
                networkNodeBuilder.addAllAddresses(addressBuilders);
                return networkNodeBuilder.build();
            }).collect(Collectors.toList());

            findNodeResponseBuilder.addAllFoundNodes(networkNodesList);
            final Messages.Response.Builder responseBuilder = Messages.Response.newBuilder()
                    .setOrigin(createSelfNetworkNodeBuilder())
                    .setFindNodeResponse(findNodeResponseBuilder);

            context.writeAndFlush(responseBuilder.build());

        } catch (SocketException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /*
     * helper - returns the builder for the "SelfNode" in wire format
     */
    private Messages.NetworkNode.Builder createSelfNetworkNodeBuilder() throws SocketException {
        final Messages.NetworkNode.Builder selfNodeBuilder = Messages.NetworkNode.newBuilder();
        selfNodeBuilder.setId(ByteString.copyFrom(selfNodeProvider.getSelf().getId()));
        final List<Messages.Address> selfAddresses = mapModelAddressToWireAddress(selfNodeProvider.getSelf().getAddresses());
        selfNodeBuilder.addAllAddresses(selfAddresses);
        return selfNodeBuilder;
    }

    /*
     * helper - simple mapper from plain addresses to wire format (model discrepancy... port should go
     */
    private List<Messages.Address> mapModelAddressToWireAddress(List<byte[]> addresses) {
        return addresses.stream().map(address -> Messages.Address.newBuilder()
                .setPort(6565)
                .setIpAddress(ByteString.copyFrom(address))
                .build())
                .collect(Collectors.toList());
    }

}
