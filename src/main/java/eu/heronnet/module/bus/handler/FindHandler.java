package eu.heronnet.module.bus.handler;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import eu.heronnet.core.model.Document;
import eu.heronnet.module.bus.command.Find;
import eu.heronnet.module.bus.command.UpdateLocalResults;
import eu.heronnet.module.bus.command.UpdateResults;
import eu.heronnet.module.kad.model.rpc.message.FindValueRequest;
import eu.heronnet.module.kad.net.Client;
import eu.heronnet.module.kad.net.SelfNodeProvider;
import eu.heronnet.module.storage.Persistence;

/**
 * @author edoardocausarano
 */
@Component
public class FindHandler {

    private static final Logger logger = LoggerFactory.getLogger(FindHandler.class);

    @Inject
    ApplicationContext applicationContext;

    @Inject
    private EventBus eventBus;

    @Inject
    private SelfNodeProvider selfNodeProvider;


    @Inject
    private Client client;

    @Inject
    private Persistence persistence;

    @Subscribe
    public void handle(Find command) throws SocketException {
        if (command.isLocal() && command.getTerm() != null) {
            List<Document> byStringKey = persistence.findByHash(Collections.singletonList(command.getHash()));
            eventBus.post(new UpdateResults(byStringKey));
        }

        if (!command.isLocal() && command.getTerm() != null) {
            String term = command.getTerm();
            try {
                FindValueRequest findValueRequest = new FindValueRequest();
                findValueRequest.setValue(term.getBytes("utf-8"));
                findValueRequest.setOrigin(selfNodeProvider.getSelf());
                client.send(findValueRequest);
            } catch (UnsupportedEncodingException e) {
                logger.error("UTF-8 charset not available... seriously?");
                eventBus.post(e);
            }
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
