package eu.heronnet.module.bus.handler;

import java.io.IOException;
import java.nio.file.Files;
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

import eu.heronnet.core.model.BinaryStatement;
import eu.heronnet.core.model.Bundle;
import eu.heronnet.core.model.Bundle.BundleBuilder;
import eu.heronnet.module.bus.command.Put;
import eu.heronnet.module.bus.command.UpdateLocalResults;
import eu.heronnet.module.storage.Persistence;

/**
 * @author edoardocausarano
 */
@Component
public class PutHandler {

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
        byte[] allBytes = Files.readAllBytes(command.getPath());
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        builder.withSubject(digest.digest(allBytes));

        builder.withStatement(new BinaryStatement("http://heronnet.eu/TR/#bytesProperty", allBytes));
        Bundle bundle = builder.build();

        persistence.put(bundle);
        eventBus.post(new UpdateLocalResults(Collections.singletonList(bundle)));
    }

    @PostConstruct
    void register() {
        eventBus.register(this);
    }
}
