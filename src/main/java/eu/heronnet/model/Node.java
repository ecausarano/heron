package eu.heronnet.model;

/**
 * The base for all "RDF" statements
 *
 * Created by edo on 07/08/15.
 */

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.*;

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
     * @return  the SHA-256 hash Id
     */
    @Override
    public final int hashCode() {
        return nodeId[3] & 0xFF |
                (nodeId[2] & 0xFF) << 8 |
                (nodeId[1] & 0xFF) << 16 |
                (nodeId[0] & 0xFF) << 24;
    }
}
