package eu.heronnet.model;

/**
 * The base for all "RDF" statements
 *
 * Created by edo on 07/08/15.
 */

import com.fasterxml.jackson.annotation.*;

import java.util.Arrays;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@JsonInclude(NON_NULL)
@JsonSubTypes({
        @JsonSubTypes.Type(value = IdentifierNode.class, name="identifierNode"),
        @JsonSubTypes.Type(value = StringNode.class, name = "stringNode"),
        @JsonSubTypes.Type(value = BinaryDataNode.class, name = "binaryDataNode"),
        @JsonSubTypes.Type(value = DateNode.class, name = "dateNode")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Node<T> {

    final byte[] nodeId;

    @JsonIgnore
    final NodeType nodeType;

    public Node(byte[] nodeId, NodeType nodeType) {
        this.nodeId = nodeId;
        this.nodeType = nodeType;
    }

    public static <T> Node<T> getNil() {
        return (Node<T>) new NilNode();
    }

    public byte[] getNodeId() {
        return nodeId;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public abstract T getData();

    public abstract String toString();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node<?> node = (Node<?>) o;

        return Arrays.equals(nodeId, node.nodeId) && nodeType == node.nodeType;

    }

    /**
     * By design the nodeId of any {@code Node} is the SHA-256 hash of the enclosed data
     *
     * @return the hash of the Node
     */
    @Override
    public int hashCode() {
        int result = Arrays.hashCode(nodeId);
        result = 31 * result + (nodeType != null ? nodeType.hashCode() : 0);
        return result;
    }

    public static class NilNode extends Node<Object> {

        public NilNode() {
            super(new byte[32], NodeType.NIL);
        }

        @Override
        public Object getData() {
            return null;
        }

        @Override
        public String toString() {
            return null;
        }
    }
}
