package eu.heronnet.module.gui.fx.controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import eu.heronnet.model.Bundle;
import eu.heronnet.model.Statement;
import eu.heronnet.model.vocabulary.HRN;
import eu.heronnet.module.bus.command.UpdateResults;
import eu.heronnet.module.gui.fx.task.ExtractDocumentMetadataService;
import eu.heronnet.module.gui.fx.task.PutFileService;
import eu.heronnet.module.gui.fx.task.SearchByPredicateService;
import eu.heronnet.module.gui.fx.task.SignBundleService;
import eu.heronnet.module.gui.fx.views.BundleView;
import eu.heronnet.module.gui.fx.views.FileUploadView;
import eu.heronnet.module.gui.fx.views.IdentityDetailsView;
import eu.heronnet.module.gui.fx.views.MainWindowView;
import eu.heronnet.module.pgp.PGPUtils;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * @author edoardocausarano
 */
public class UIController {

    private static final Logger logger = LoggerFactory.getLogger(UIController.class);

    @Inject
    private ExtractDocumentMetadataService extractDocumentMetadataService;
    @Inject
    private SearchByPredicateService searchByPredicateService;
    @Inject
    private SignBundleService signBundleService;
    @Inject
    private PutFileService putFileService;
    @Inject
    private PGPUtils pgpUtils;
    @Inject
    @Qualifier(value = "mainBus")
    EventBus mainBus;

    private volatile BundleView bundleView;
    private volatile MainWindowView mainWindowView;
    private volatile FileUploadView fileUploadView;
    private volatile IdentityDetailsView identityDetailsView;

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

    public void setFileUploadView(FileUploadView fileUploadView) {
        this.fileUploadView = fileUploadView;
    }

    public void setIdentityDetailsView(IdentityDetailsView identityDetailsView) {
        this.identityDetailsView = identityDetailsView;
    }

    public void distributedSearch(String text) {
        searchByPredicateService.reset();
        searchByPredicateService.setQuery(Collections.singletonList(text));
        searchByPredicateService.setLocal(false);
        searchByPredicateService.setOnSucceeded(event -> mainWindowView.setResultView(searchByPredicateService.getValue()));
        searchByPredicateService.start();
    }

    public void localSearch(String text) {
        searchByPredicateService.reset();
        searchByPredicateService.setQuery(Collections.singletonList(text));
        searchByPredicateService.setLocal(true);
        searchByPredicateService.setOnSucceeded(event -> {
            logger.debug("done: " + event.getSource().getValue());
            bundleView.set(searchByPredicateService.getValue());
        });
        searchByPredicateService.start();
    }

    public boolean isUserSigningEnabled() {
        return pgpUtils.hasPrivateKey();
    }

    public void signBundle(Bundle item) {
        signBundleService.reset();
        signBundleService.setBundle(item);
        signBundleService.setOnSucceeded(event -> logger.debug("done:" + event.getSource().getValue()));
        signBundleService.start();
    }

    public void addFile(File file) {
        extractDocumentMetadataService.reset();
        extractDocumentMetadataService.setFile(file);
        extractDocumentMetadataService.setOnSucceeded(event -> {
            fileUploadView.setFields(extractDocumentMetadataService.getValue());
        });
        extractDocumentMetadataService.start();
    }

    public void putFile(List<Statement> statements, Path path) {
        putFileService.reset();
        putFileService.setStatements(statements);
        putFileService.setPath(path);
        putFileService.setOnSucceeded(event -> {
            logger.debug("successfully sent file to network");
        });
        putFileService.start();
    }

    public void downloadBundle(Bundle item) {
        logger.debug("Downloading item id={}", item.getSubject().getNodeId());
        searchByPredicateService.reset();
        searchByPredicateService.setHash(item.getSubject().getNodeId());
        searchByPredicateService.setQuery(Collections.singletonList(HRN.BINARY.toString()));
        searchByPredicateService.start();
        searchByPredicateService.setOnSucceeded(event -> {
            logger.debug("HORRAAAAAY, found files");
        });

    }

    @Subscribe
    public void updateSearchResults(UpdateResults domainBundles) {
        Platform.runLater(() -> {
            mainWindowView.setResultView(domainBundles.getBundles());
        });
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
