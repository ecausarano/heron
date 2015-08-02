package eu.heronnet.module.storage.binding;

import org.springframework.stereotype.Component;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import eu.heronnet.core.model.Statement;
import eu.heronnet.core.model.Triple;

/**
 * @author edoardocausarano
 */
@Component
public class StringTripleBinding extends TupleBinding<Triple<String>> {

    @Override
    public Triple<String> entryToObject(TupleInput input) {
        byte[] subjectId = new byte[32];
        input.read(subjectId);
        return new Triple<>(subjectId, new Statement<>(input.readString(), input.readString()));
    }

    @Override
    public void objectToEntry(Triple<String> object, TupleOutput output) {
        output.write(object.getSubject());
        Statement<String> statement = object.getStatement();
        output.writeString(statement.getPredicate());
        output.writeString(statement.getObject());
    }
}
