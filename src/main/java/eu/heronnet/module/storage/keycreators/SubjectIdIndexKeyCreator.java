package eu.heronnet.module.storage.keycreators;

import javax.inject.Inject;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;
import eu.heronnet.model.Bundle;
import eu.heronnet.module.storage.binding.BundleBinding;
import org.springframework.stereotype.Component;

/**
 * @author edoardocausarano
 */
@Component
public class SubjectIdIndexKeyCreator implements SecondaryKeyCreator {
    @Inject
    BundleBinding bundleBinding;

    @Override
    public boolean createSecondaryKey(SecondaryDatabase secondary, DatabaseEntry key, DatabaseEntry data, DatabaseEntry result) {
        Bundle bundle = bundleBinding.entryToObject(data);
        byte[] nodeId = bundle.getSubject().getNodeId();
        if (nodeId != null) {
            result.setData(nodeId);
            return true;
        } else {
            return false;
        }
    }
}
