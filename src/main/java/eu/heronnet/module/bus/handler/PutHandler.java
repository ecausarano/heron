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
import java.util.Collections;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import eu.heronnet.model.BinaryDataNode;
import eu.heronnet.model.Bundle;
import eu.heronnet.model.IdentifierNode;
import eu.heronnet.model.Statement;
import eu.heronnet.model.builder.BundleBuilder;
import eu.heronnet.model.vocabulary.HRN;
import eu.heronnet.module.bus.command.Put;
import eu.heronnet.module.bus.command.PutBundle;
import eu.heronnet.module.bus.command.UpdateLocalResults;
import eu.heronnet.module.storage.Persistence;
import eu.heronnet.module.storage.util.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author edoardocausarano
 */
@Component
public class PutHandler {

    public static final String HERON_BINARY_PREDICATE = "http://heronnet.eu/0.1/bytesProperty";
    private static final Logger logger = LoggerFactory.getLogger(PutHandler.class);
    @Inject
    private EventBus eventBus;

    @Inject
    private Persistence persistence;

    @Subscribe
    public void handle(PutBundle command) {
        persistence.put(command.getBundle());
        eventBus.post(new UpdateLocalResults(Collections.singletonList(command.getBundle())));
    }

    @Subscribe
    public void handle(Put command) throws IOException, NoSuchAlgorithmException {
        BundleBuilder builder = command.getBuilder();

        if (command.getPath() != null) {
            try (InputStream inputStream = Files.newInputStream(command.getPath(), StandardOpenOption.READ)) {
                final byte[] buffer = new byte[4096];
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                while (inputStream.read(buffer) != -1) {
                    digest.update(buffer);
                }
                final byte[] fileHash = digest.digest();
                builder.withSubject(new IdentifierNode(fileHash));
                builder.withStatement(new Statement(
                        HRN.BINARY,
                        // TODO - introduce a type that doesn't contain the bytes but a reference to the file to be streamed
                        new BinaryDataNode(fileHash, Files.readAllBytes(command.getPath()))));
            }
        }

        Bundle bundle = builder.build();
        if (logger.isDebugEnabled()) {
            Path path = command.getPath();
            if (path != null) {
                logger.debug("put file path={}", path);
            } else {
                logger.debug("put bundle id={}", HexUtil.bytesToHex(bundle.getNodeId()));
            }
        }
        persistence.put(bundle);
        eventBus.post(new UpdateLocalResults(Collections.singletonList(bundle)));
    }

    @PostConstruct
    void register() {
        eventBus.register(this);
    }
}
