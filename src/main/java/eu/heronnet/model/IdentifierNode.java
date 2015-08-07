package eu.heronnet.model;

import eu.heronnet.module.storage.util.HexUtil;

/**
 * Created by edo on 07/08/15.
 */
public class IdentifierNode extends Node {

    public IdentifierNode(byte[] nodeId) {
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
