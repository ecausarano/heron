package eu.heronnet.model;

/**
 * Created by edo on 07/08/15.
 */
public abstract class Node<T> {

    final byte[] nodeId;
    final NodeType nodeType;

    public Node(byte[] nodeId, NodeType nodeType) {
        this.nodeId = nodeId;
        this.nodeType = nodeType;
    }

    public byte[] getNodeId() {
        return nodeId;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public abstract T getData();

    public abstract String toString();

}
