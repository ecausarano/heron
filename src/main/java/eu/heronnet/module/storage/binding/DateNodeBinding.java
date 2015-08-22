package eu.heronnet.module.storage.binding;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import eu.heronnet.model.DateNode;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by edo on 07/08/15.
 */
@Component
public class DateNodeBinding extends TupleBinding<DateNode> {
    @Override
    public DateNode entryToObject(TupleInput tupleInput) {
        byte[] nodeId = new byte[32];
        tupleInput.read(nodeId);
        return new DateNode(nodeId, new Date(tupleInput.readLong()));
    }

    @Override
    public void objectToEntry(DateNode dateNode, TupleOutput tupleOutput) {
        tupleOutput.write(dateNode.getNodeId());
        tupleOutput.writeLong(dateNode.getData().getTime());
    }
}
