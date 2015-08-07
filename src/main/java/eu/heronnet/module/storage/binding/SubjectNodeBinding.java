package eu.heronnet.module.storage.binding;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import eu.heronnet.model.IdentifierNode;

/**
 * Created by edo on 07/08/15.
 */
public class SubjectNodeBinding extends TupleBinding<IdentifierNode> {
    @Override
    public IdentifierNode entryToObject(TupleInput tupleInput) {
        byte[] nodeId = new byte[32];
        tupleInput.read(nodeId);
        return new IdentifierNode(nodeId);
    }

    @Override
    public void objectToEntry(IdentifierNode identifierNode, TupleOutput tupleOutput) {
        tupleOutput.write(identifierNode.getNodeId());
    }
}
