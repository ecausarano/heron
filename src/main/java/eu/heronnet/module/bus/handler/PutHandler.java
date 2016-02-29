package eu.heronnet.module.bus.handler;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import eu.heronnet.model.BinaryDataNode;
import eu.heronnet.model.Bundle;
import eu.heronnet.model.BundleBuilder;
import eu.heronnet.model.IdentifierNode;
import eu.heronnet.model.Statement;
import eu.heronnet.model.vocabulary.HRN;
import eu.heronnet.module.bus.command.Put;
import eu.heronnet.module.bus.command.PutBundle;
import eu.heronnet.module.storage.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author edoardocausarano
 */
public class PutHandler {

    private static final Logger logger = LoggerFactory.getLogger(PutHandler.class);

    @Inject
    @Qualifier(value = "mainBus")
    private EventBus mainBus;

    @Inject
    @Qualifier(value = "distributedStorage")
    private Persistence persistence;

    @Subscribe
    public void handle(PutBundle command) {
        persistence.put(command.getBundle());
    }

    @Subscribe
    public void handle(Put command) throws IOException, NoSuchAlgorithmException {
        BundleBuilder binaryBundleBuilder = new BundleBuilder();
        if (command.getPath() != null) {
            try (InputStream inputStream = Files.newInputStream(command.getPath(), StandardOpenOption.READ)) {
                final byte[] buffer = new byte[4096];
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                while (inputStream.read(buffer) != -1) {
                    digest.update(buffer);
                }
                final byte[] fileHash = digest.digest();
                binaryBundleBuilder.withSubject(new IdentifierNode(fileHash));
                binaryBundleBuilder.withStatement(new Statement(
                        HRN.BINARY,
                        // TODO - introduce a type that doesn't contain the bytes but a reference to the file to be streamed
                        new BinaryDataNode(fileHash, Files.readAllBytes(command.getPath()))));
            }
        }

        Bundle binaryBundle = binaryBundleBuilder.build();
        // NB the subject of the binary bundle and the bundle of statements is the same
        final BundleBuilder bundleBuilder = command.getBuilder().withSubject(binaryBundle.getSubject());
        persistence.put(binaryBundle);
        persistence.put(bundleBuilder.build());

        if (logger.isDebugEnabled()) {
            Path path = command.getPath();
            logger.debug("put file path={}", path);
        }

    }

    @PostConstruct
    void register() {
        mainBus.register(this);
    }
}
