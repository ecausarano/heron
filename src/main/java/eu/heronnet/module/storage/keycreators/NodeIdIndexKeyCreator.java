package eu.heronnet.module.storage.keycreators;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryMultiKeyCreator;
import eu.heronnet.model.Bundle;
import eu.heronnet.module.storage.binding.BundleBinding;

import javax.inject.Inject;
import java.util.Set;

/**
 * Created by edo on 07/08/15.
 */
public class NodeIdIndexKeyCreator implements SecondaryMultiKeyCreator {

    @Inject
    BundleBinding bundleBinding;

    @Override
    public void createSecondaryKeys(SecondaryDatabase secondary, DatabaseEntry key, DatabaseEntry data, Set<DatabaseEntry> results) {
        Bundle bundle = bundleBinding.entryToObject(data);
        bundle.getStatements().stream().forEach(statement -> results.add(new DatabaseEntry(statement.getPredicate().getNodeId())));

    }
}
