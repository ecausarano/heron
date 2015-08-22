package eu.heronnet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.heronnet.module.storage.util.HexUtil;

/**
 * A {@link Node} subtype used for representing identifiers. Always found in the
 * {@code subject} of a {@link Bundle}
 *
 * Created by edo on 07/08/15.
 */
public class IdentifierNode extends Node {

    public IdentifierNode(@JsonProperty("nodeId") byte[] nodeId) {
        super(nodeId, NodeType.IDENTIFIER);
    }

    public static IdentifierNode anyId() {
        return new IdentifierNode(new byte[32]);
    }

    @JsonIgnore
    @Override
    public Object getData() {
        return nodeId;
    }

    @Override
    public String toString() {
        return HexUtil.bytesToHex(nodeId);
    }

}
