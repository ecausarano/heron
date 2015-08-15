package eu.heronnet.module.bus.handler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import eu.heronnet.model.*;
import eu.heronnet.model.builder.BundleBuilder;
import eu.heronnet.module.bus.command.Put;
import eu.heronnet.module.bus.command.UpdateLocalResults;
import eu.heronnet.module.storage.Persistence;

/**
 * @author edoardocausarano
 */
@Component
public class PutHandler {

    public static final String HERON_BINARY_PREDICATE = "http://heronnet.eu/TR/#bytesProperty";
    private static final Logger logger = LoggerFactory.getLogger(PutHandler.class);
    @Inject
    private EventBus eventBus;

    @Inject
    private Persistence persistence;

    @Subscribe
    public void handle(Put command) throws IOException, NoSuchAlgorithmException {
        if (logger.isDebugEnabled()) {
            logger.debug("put={}", command.getPath());
        }

        BundleBuilder builder = command.getBuilder();

        try (InputStream inputStream = Files.newInputStream(command.getPath(), StandardOpenOption.READ)) {
            final byte[] buffer = new byte[4096];
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            while (inputStream.read(buffer) != -1) {
                digest.update(buffer);
            }
            final byte[] fileHash = digest.digest();
            byte[] statementHash = digest.digest(HERON_BINARY_PREDICATE.getBytes());
            builder.withSubject(new IdentifierNode(fileHash));
            builder.withStatement(new Statement(
                    new StringNode(statementHash, HERON_BINARY_PREDICATE),
                    // TODO - introduce a type that doesn't contain the bytes but a reference to the file to be streamed
                    new BinaryDataNode(fileHash, Files.readAllBytes(command.getPath()))));
        }
        Bundle bundle = builder.build();
        persistence.put(bundle);
        eventBus.post(new UpdateLocalResults(Collections.singletonList(bundle)));
    }

    @PostConstruct
    void register() {
        eventBus.register(this);
    }
}
