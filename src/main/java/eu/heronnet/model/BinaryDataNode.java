package eu.heronnet.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.heronnet.module.storage.util.HexUtil;

/**
 * Created by edo on 07/08/15.
 */
public class BinaryDataNode extends Node {

    private final byte[] data;

    public BinaryDataNode(@JsonProperty("nodeId") byte[] nodeId, @JsonProperty("data") byte[] data) {
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
