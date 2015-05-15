package eu.heronnet.module.bus.handler;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import eu.heronnet.core.model.Document;
import eu.heronnet.module.bus.command.Find;
import eu.heronnet.module.bus.command.UpdateLocalResults;
import eu.heronnet.module.bus.command.UpdateResults;
import eu.heronnet.module.network.dht.DHTService;
import eu.heronnet.module.storage.Persistence;

/**
 * @author edoardocausarano
 */
@Singleton
public class FindHandler {

    private static final Logger logger = LoggerFactory.getLogger(FindHandler.class);

    @Inject
    EventBus eventBus;

    @Inject
    private Persistence persistence;

    @Inject
    private DHTService dhtService;

    @Subscribe
    public void handle(Find command) {
        if (!command.isLocal() && command.getFields().size() != 0) {
            List<Document> documents = persistence.findDocumentByFieldSpec(command.getFields());
            eventBus.post(new UpdateResults(documents));
        }

        if (!command.isLocal() && command.getTerm() != null) {
            List<Document> byStringKey = persistence.findByStringKey(Collections.singletonList(command.getTerm()));
            eventBus.post(new UpdateResults(byStringKey));
        }

        if (command.isLocal()) {
            List<Document> documents = persistence.getAll();
            eventBus.post(new UpdateLocalResults(documents));
        }
    }

    @PostConstruct
    void register() {
        eventBus.register(this);
    }

}
