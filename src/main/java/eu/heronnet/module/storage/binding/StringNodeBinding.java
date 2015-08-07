package eu.heronnet.module.storage.binding;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import eu.heronnet.model.StringNode;

/**
 * Created by edo on 07/08/15.
 */
public class StringNodeBinding extends TupleBinding<StringNode> {
    @Override
    public StringNode entryToObject(TupleInput tupleInput) {
        byte[] nodeId = new byte[32];
        tupleInput.read(nodeId);
        return new StringNode(nodeId, tupleInput.readString());

    }

    @Override
    public void objectToEntry(StringNode stringNode, TupleOutput tupleOutput) {
        tupleOutput.write(stringNode.getNodeId());
        tupleOutput.writeString(stringNode.getData());

    }
}
