package eu.heronnet.model;

/**
 * Created by edo on 07/08/15.
 */

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@JsonInclude(NON_NULL)
//@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = IdentifierNode.class, name="identifierNode"),
        @JsonSubTypes.Type(value = StringNode.class, name = "stringNode"),
        @JsonSubTypes.Type(value = BinaryDataNode.class, name = "binaryDataNode"),
        @JsonSubTypes.Type(value = DateNode.class, name = "dateNode")
})
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

    @JsonIgnore
    public abstract T getData();

    public abstract String toString();

}
