package eu.heronnet.module.gui.fx.controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import eu.heronnet.model.Bundle;
import eu.heronnet.module.bus.command.UpdateResults;
import eu.heronnet.module.gui.fx.task.SearchByPredicate;
import eu.heronnet.module.gui.fx.task.SignBundleService;
import eu.heronnet.module.gui.fx.views.BundleView;
import eu.heronnet.module.gui.fx.views.MainWindowView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

/**
 * @author edoardocausarano
 */
public class UIController {

    private static final Logger logger = LoggerFactory.getLogger(UIController.class);

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private SignBundleService signBundleService;

    @Inject
    @Qualifier(value = "mainBus")
    EventBus mainBus;

    private volatile BundleView bundleView;
    private volatile MainWindowView mainWindowView;

    @PostConstruct
    public void postConstruct() {
        mainBus.register(this);
    }

    public void setBundleView(BundleView bundleView) {
        this.bundleView = bundleView;
    }

    public void setMainWindowView(MainWindowView mainWindowView) {
        this.mainWindowView = mainWindowView;
    }

    public void distributedSearch(String text) {
        final SearchByPredicate searchByPredicate = applicationContext.getBean("searchByPredicate", SearchByPredicate.class);
        searchByPredicate.setQuery(text);
        searchByPredicate.setLocal(false);
        searchByPredicate.setOnSucceeded(event -> mainWindowView.setResultView(searchByPredicate.getValue()));
        searchByPredicate.start();
    }

    public void localSearch(String text) {
        final SearchByPredicate searchByPredicate = applicationContext.getBean("searchByPredicate", SearchByPredicate.class);
        searchByPredicate.setQuery(text);
        searchByPredicate.setLocal(true);
        searchByPredicate.setOnSucceeded(event -> {
            logger.debug("done: " + event.getSource().getValue());
            bundleView.set(searchByPredicate.getValue());
        });
        searchByPredicate.start();
    }

    public boolean isUserSigningEnabled() {
        return true;
    }

    public void signBundle(Bundle item) {
        signBundleService.setBundle(item);
        signBundleService.setOnSucceeded(event -> logger.debug("done:" + event.getSource().getValue()));
        signBundleService.start();
    }

    public void downloadBundle(Bundle item) {
        throw new RuntimeException("download not implemented");
    }

    @Subscribe
    public void updateSearchResults(UpdateResults domainBundles) {
        final List<Bundle> bundles = domainBundles.getBundles();
        mainWindowView.setResultView(bundles);

    }
    public void updatePublicKeyList() {
//        Task<Void> listPublicKeysTask = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                List<Bundle> bundles = persistence.findByPredicate(Collections.singletonList(HRN.PUBLIC_KEY));
//                logger.debug("Found {} public keys", bundles.size());
//                // TODO - code to update the observable list of public keys
//                return null;
//            }
//        };
    }

}
