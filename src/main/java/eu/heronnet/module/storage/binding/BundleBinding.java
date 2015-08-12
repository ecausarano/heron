package eu.heronnet.module.storage.binding;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import eu.heronnet.model.*;

/**
 * Created by edo on 07/08/15.
 */
public class BundleBinding extends TupleBinding<Bundle> {
    @Override
    public Bundle entryToObject(TupleInput tupleInput) {

        byte[] bundleId = new byte[32];
        tupleInput.read(bundleId);

        Bundle bundle = new Bundle(new IdentifierNode(bundleId));

        int size = tupleInput.readInt();
        new HashSet<Statement>(size);
        for (int i = 0; i < size; i++) {
            byte[] predicateId = new byte[32];
            tupleInput.read(predicateId);
            String predicateData = tupleInput.readString();
            StringNode predicate = new StringNode(predicateId, predicateData);

            NodeType nodeType = NodeType.values()[tupleInput.readInt()];
            byte[] objectId = new byte[32];
            tupleInput.read(objectId);
            switch (nodeType) {
                case STRING:
                    StringNode stringNode = new StringNode(objectId, tupleInput.readString());
                    bundle.add(new Statement(predicate, stringNode));
                    break;
                case DATE:
                    DateNode dateNode = new DateNode(objectId, new Date(tupleInput.readLong()));
                    bundle.add(new Statement(predicate, dateNode));
                    break;
                case IDENTIFIER:
                    IdentifierNode identifierNode = new IdentifierNode(objectId);
                    bundle.add(new Statement(predicate, identifierNode));
                    break;
                case BINARY:
                    byte[] bytes = new byte[tupleInput.readInt()];
                    tupleInput.read(bytes);
                    new BinaryDataNode(objectId, bytes);
                    break;
                default:
                    throw new RuntimeException("nope cannot ever happen");
            }

        }

        return bundle;
    }

    @Override
    public void objectToEntry(Bundle bundle, TupleOutput tupleOutput) {
        IdentifierNode subject = bundle.getSubject();
        Set<Statement> statements = bundle.getStatements();

        tupleOutput.write(subject.getNodeId());
        // 0. how many statements in the bundle
        tupleOutput.writeInt(statements.size());

        for (Statement statement : statements) {
            StringNode predicate = statement.getPredicate();
            // 1. predicateId
            byte[] nodeId = predicate.getNodeId();
            tupleOutput.write(nodeId);

            // 2. predicate String
            String predicateData = predicate.getData();
            tupleOutput.writeString(predicateData);

            Node object = statement.getObject();
            // 3. object type - WARNING changing the order in the ENUM will fuck your database up!
            NodeType objectNodeType = object.getNodeType();
            tupleOutput.writeInt(objectNodeType.ordinal());

            // 4. objectNodeId
            tupleOutput.write(object.getNodeId());

            // 5. the data value
            switch (objectNodeType) {
                case STRING:
                    tupleOutput.writeString(((StringNode) object).getData());
                    break;
                case DATE:
                    tupleOutput.writeLong(((DateNode) object).getData().getTime());
                    break;
                case IDENTIFIER:
                    tupleOutput.write(object.getNodeId());
                    break;
                case BINARY:
                    byte[] data = ((BinaryDataNode) object).getData();
                    tupleOutput.writeInt(data.length);
                    tupleOutput.write(data);
                    break;
                default:
                    throw new RuntimeException("this cannot ever happen");
            }
        }
    }
}
