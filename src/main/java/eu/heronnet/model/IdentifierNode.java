package eu.heronnet.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.heronnet.module.storage.util.HexUtil;

/**
 * Created by edo on 07/08/15.
 */
public class IdentifierNode extends Node {

    public IdentifierNode(@JsonProperty("nodeId") byte[] nodeId) {
        super(nodeId, NodeType.IDENTIFIER);
    }

    @Override
    public Object getData() {
        return nodeId;
    }

    @Override
    public String toString() {
        return HexUtil.bytesToHex(nodeId);
    }

}
