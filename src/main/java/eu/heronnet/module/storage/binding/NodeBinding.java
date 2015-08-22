package eu.heronnet.module.storage.binding;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import eu.heronnet.model.*;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by edo on 07/08/15.
 */
@Component
public class NodeBinding extends TupleBinding<Node> {
    @Override
    public Node entryToObject(TupleInput input) {
        // 1. the id
        byte[] nodeId = new byte[32];
        input.read(nodeId);

        // 2. the node type
        NodeType nodeType = NodeType.values()[input.readInt()];

        // 3. the data value
        switch (nodeType) {
            case STRING:
                return new StringNode(nodeId, input.readString());
            case DATE:
                return new DateNode(nodeId, new Date(input.readLong()));
            case IDENTIFIER:
                return new IdentifierNode(nodeId);
            case BINARY:
                byte[] binaryData = new byte[input.readInt()];
                input.read(binaryData);
                return new BinaryDataNode(nodeId, binaryData);
            default:
                throw new RuntimeException("nope cannot ever happen");
        }
    }

    @Override
    public void objectToEntry(Node object, TupleOutput output) {
        // 1. the id
        output.write(object.getNodeId());

        NodeType objectNodeType = object.getNodeType();

        // 2. the node type
        output.writeInt(objectNodeType.ordinal());

        // 3. the data item
        switch (objectNodeType) {
            case STRING:
                output.writeString(((StringNode) object).getData());
                break;
            case DATE:
                output.writeLong(((DateNode) object).getData().getTime());
                break;
            case IDENTIFIER:
                output.write(object.getNodeId());
                break;
            case BINARY:
                byte[] data = ((BinaryDataNode) object).getData();
                output.writeInt(data.length);
                output.write(data);
                break;
            default:
                throw new RuntimeException("this cannot ever happen");
        }

    }
}
