package eu.heronnet.module.gui.fx.controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.security.PublicKey;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import eu.heronnet.model.Bundle;
import eu.heronnet.model.Statement;
import eu.heronnet.model.vocabulary.HRN;
import eu.heronnet.module.bus.command.Find;
import eu.heronnet.module.bus.command.PutBundle;
import eu.heronnet.module.bus.command.UpdateLocalResults;
import eu.heronnet.module.bus.command.UpdateResults;
import eu.heronnet.module.gui.fx.views.IdentityDetails;
import eu.heronnet.module.pgp.PGPUtils;
import eu.heronnet.module.storage.Persistence;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author edoardocausarano
 */
public class UIController {

    private static final Logger logger = LoggerFactory.getLogger(UIController.class);

    @Inject
    EventBus eventBus;

    @Inject
    MainWindowController mainWindowController;
    @Inject
    private IdentityDetails identityDetails;


    @Inject
    private Callback controllerFactory;
    @Inject
    private ExecutorService executor;
    @Inject
    private Persistence persistence;
    @Inject
    private PGPUtils pgpUtils;

    void searchForText(String text) {
        eventBus.post(new Find(text));
    }

    @PostConstruct
    void postConstruct() {
        eventBus.register(this);
    }

    @Subscribe
    public void updateSearchResults(UpdateResults results) {
        Platform.runLater(() -> {
            ObservableList<Bundle> items = mainWindowController.getResultItems();
            items.clear();
            // TODO - merge bundles with same subjectId
            items.addAll(results.getBundles());
        });
    }

    public boolean isUserSigningEnabled() {
        return true;
    }

    public void signBundle(Bundle item) {
        Task<Statement> signBundleTask = new Task<Statement>() {
            @Override
            protected Statement call() throws Exception {
                return pgpUtils.createSignature(item, "password".toCharArray());
            }
        };

        signBundleTask.setOnSucceeded(workerStateEvent -> {
            eventBus.post(new PutBundle(item));
        });
        signBundleTask.setOnFailed(event1 -> {
            logger.error("failed");
        });
        executor.execute(signBundleTask);
    }

    public void downloadBundle(Bundle item) {
        throw new RuntimeException("download not implemented");
    }

    public void updatePublicKeyList() {
        Task<Void> listPublicKeysTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<Bundle> bundles = persistence.findByPredicate(Collections.singletonList(HRN.PUBLIC_KEY));
                logger.debug("Found {} public keys", bundles.size());
                // TODO - code to update the observable list of public keys
                return null;
            }
        };
        executor.execute(listPublicKeysTask);
    }

    @Subscribe
    public void updateLocalStorage(UpdateLocalResults results) {

    }

}
