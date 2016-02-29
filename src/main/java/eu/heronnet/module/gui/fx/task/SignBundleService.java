package eu.heronnet.module.gui.fx.task;

import javax.inject.Inject;

import eu.heronnet.model.Bundle;
import eu.heronnet.model.BundleBuilder;
import eu.heronnet.model.Statement;
import eu.heronnet.module.pgp.PGPUtils;
import eu.heronnet.module.storage.Persistence;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author edoardocausarano
 */
public class SignBundleService extends Service<Bundle> {

    private static final Logger logger = LoggerFactory.getLogger(SignBundleService.class);

    @Inject
    private PGPUtils pgpUtils;
    @Inject
    @Qualifier(value = "distributedStorage")
    private Persistence persistence;

    private Bundle bundle;

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

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
                    throw e;
                }
            }
        };
    }

    @Override
    protected void succeeded() {
        logger.debug("Succeeded signing bundle: {}", bundle.getSubject().toString());
        final Bundle value = getValue();
        persistence.put(value);

    }
}

//        Task<Statement> signBundleTask = new Task<Statement>() {
//            @Override
//            protected Statement call() throws Exception {
//                items.forEach(fieldRow -> builder.withStatement(new Statement(
//                        IRIBuilder.withString(fieldRow.getName()),
//                        StringNodeBuilder.withString(fieldRow.getValue()))));
//                Bundle bundle = builder.build();
//                return pgpUtils.createSignature(bundle, "password".toCharArray());
//            }
//        };

//        signBundleTask.setOnSucceeded(workerStateEvent ->{
//            builder.withStatement(signBundleTask.getValue()) ;
//            Put put = new Put(builder, path);
//            eventBus.post(put);
//        });
//        executor.execute(signBundleTask);
