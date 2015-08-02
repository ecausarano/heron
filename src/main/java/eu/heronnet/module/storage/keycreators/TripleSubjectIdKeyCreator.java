package eu.heronnet.module.storage.keycreators;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;

import eu.heronnet.core.model.Triple;
import eu.heronnet.module.storage.binding.StringTripleBinding;

/**
 * @author edoardocausarano
 */
@Component
public class TripleSubjectIdKeyCreator implements SecondaryKeyCreator {

    @Inject
    StringTripleBinding tripleBinding;

    @Override
    public boolean createSecondaryKey(SecondaryDatabase secondary, DatabaseEntry key, DatabaseEntry data, DatabaseEntry result) {
        Triple<String> triple = tripleBinding.entryToObject(data);
        result.setData(triple.getSubject());
        return true;
    }
}
