package eu.heronnet.module.storage.binding;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import eu.heronnet.model.BinaryDataNode;
import eu.heronnet.model.Bundle;
import eu.heronnet.model.BundleBuilder;
import eu.heronnet.model.DateNode;
import eu.heronnet.model.IRI;
import eu.heronnet.model.IdentifierNode;
import eu.heronnet.model.Node;
import eu.heronnet.model.NodeType;
import eu.heronnet.model.Statement;
import eu.heronnet.model.StringNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by edo on 07/08/15.
 */
@Component
public class BundleBinding extends TupleBinding<Bundle> {

    private static final Logger logger = LoggerFactory.getLogger(BundleBinding.class);

    @Override
    public Bundle entryToObject(TupleInput tupleInput) {

        byte[] identifierId = new byte[32];
        tupleInput.read(identifierId);

        BundleBuilder bundleBuilder = new BundleBuilder();
        bundleBuilder.withSubject(new IdentifierNode(identifierId));

        int size = tupleInput.readInt();
        new HashSet<Statement>(size);
        for (int i = 0; i < size; i++) {
            byte[] predicateId = new byte[32];
            tupleInput.read(predicateId);
            String predicateData = tupleInput.readString();
            IRI predicate = new IRI(predicateId, predicateData);

            NodeType nodeType = NodeType.values()[tupleInput.readInt()];
            byte[] objectId = new byte[32];
            tupleInput.read(objectId);
            switch (nodeType) {
                case STRING:
                    StringNode stringNode = new StringNode(objectId, tupleInput.readString());
                    bundleBuilder.withStatement((new Statement(predicate, stringNode)));
                    break;
                case DATE:
                    DateNode dateNode = new DateNode(objectId, new Date(tupleInput.readLong()));
                    bundleBuilder.withStatement(new Statement(predicate, dateNode));
                    break;
                case IDENTIFIER:
                    IdentifierNode identifierNode = new IdentifierNode(objectId);
                    bundleBuilder.withStatement(new Statement(predicate, identifierNode));
                    break;
                case BINARY:
                    byte[] bytes = new byte[tupleInput.readInt()];
                    tupleInput.read(bytes);
                    BinaryDataNode binaryDataNode = new BinaryDataNode(objectId, bytes);
                    // do stuff with binary
                    break;
                default:
                    throw new RuntimeException("nope cannot ever happen");
            }

        }
        return bundleBuilder.build();
    }

    @Override
    public void objectToEntry(Bundle bundle, TupleOutput tupleOutput) {
        IdentifierNode subject = bundle.getSubject();
        Set<Statement> statements = bundle.getStatements();

        tupleOutput.write(subject.getNodeId());
        // 0. how many statements in the bundle
        tupleOutput.writeInt(statements.size());

        for (Statement statement : statements) {
            IRI predicate = statement.getPredicate();
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
