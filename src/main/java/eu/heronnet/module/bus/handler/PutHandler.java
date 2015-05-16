package eu.heronnet.module.bus.handler;

import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import eu.heronnet.core.model.Document;
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
    public void handle(Put command) {
        Document document = command.getDocumentBuilder().build();
        logger.debug("put={}", document.getHash());
        persistence.put(document);
        eventBus.post(new UpdateLocalResults(Collections.singletonList(document)));
    }

    @PostConstruct
    void register() {
        eventBus.register(this);
    }
}