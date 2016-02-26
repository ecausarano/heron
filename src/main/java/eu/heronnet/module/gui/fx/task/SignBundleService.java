package eu.heronnet.module.gui.fx.task;

import javax.inject.Inject;

import eu.heronnet.model.Bundle;
import eu.heronnet.model.Statement;
import eu.heronnet.model.builder.BundleBuilder;
import eu.heronnet.module.pgp.PGPUtils;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author edoardocausarano
 */
public class SignBundleService extends Service<Bundle> {

    private static final Logger logger = LoggerFactory.getLogger(SignBundleService.class);

    @Inject
    private PGPUtils pgpUtils;

    private Bundle bundle;

    @Override
    protected Task<Bundle> createTask() {
        return new Task<Bundle>() {
            @Override
            protected Bundle call() throws Exception {
                try {
                    final Statement signature = pgpUtils.createSignature(bundle, "password".toCharArray());
                    final BundleBuilder bundleBuilder = new BundleBuilder();
                    bundleBuilder.withBundle(bundle);
                    bundleBuilder.withStatement(signature);
                    return bundleBuilder.build();
                } catch (Exception e) {
                    logger.error("Error while signing bundle: {}", e.getMessage());
                    return BundleBuilder.emptyBundle();
                }
            }
        };
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
