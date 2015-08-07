package eu.heronnet.model;

import eu.heronnet.module.storage.util.HexUtil;

/**
 * Created by edo on 07/08/15.
 */
public class BinaryDataNode extends Node {

    private final byte[] data;

    public BinaryDataNode(byte[] nodeId, byte[] data) {
        super(nodeId, NodeType.BINARY);
        this.data = data;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return HexUtil.bytesToHex(nodeId);
    }
}
