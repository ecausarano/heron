package eu.heronnet.module.bus.handler;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import eu.heronnet.model.*;
import eu.heronnet.model.builder.BundleBuilder;
import eu.heronnet.module.bus.command.Put;
import eu.heronnet.module.bus.command.UpdateLocalResults;
import eu.heronnet.module.storage.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

/**
 * @author edoardocausarano
 */
@Component
public class PutHandler {

    private static final Logger logger = LoggerFactory.getLogger(PutHandler.class);
    public static final String HERON_BINARY_PREDICATE = "http://heronnet.eu/TR/#bytesProperty";

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
        byte[] allBytes = Files.readAllBytes(command.getPath());

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        byte[] fileHash = digest.digest(allBytes);
        byte[] statementHash = digest.digest(HERON_BINARY_PREDICATE.getBytes());

        builder.withSubject(new IdentifierNode(fileHash));
        builder.withStatement(new Statement(
                new StringNode(statementHash, HERON_BINARY_PREDICATE),
                new BinaryDataNode(fileHash, allBytes)));

        Bundle bundle = builder.build();

        persistence.put(bundle);
        eventBus.post(new UpdateLocalResults(Collections.singletonList(bundle)));
    }

    @PostConstruct
    void register() {
        eventBus.register(this);
    }
}
