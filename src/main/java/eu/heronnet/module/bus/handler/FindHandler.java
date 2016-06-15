package eu.heronnet.module.bus.handler;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import eu.heronnet.model.Bundle;
import eu.heronnet.module.bus.command.Find;
import eu.heronnet.module.bus.command.UpdateResults;
import eu.heronnet.module.kad.net.SelfNodeProvider;
import eu.heronnet.module.storage.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author edoardocausarano
 */
//@Component
public class FindHandler {

    private static final Logger logger = LoggerFactory.getLogger(FindHandler.class);

    private static final Pattern splitter = Pattern.compile("\\s");

    @Inject
    ApplicationContext applicationContext;

    @Inject
    @Qualifier(value = "mainBus")
    private EventBus mainBus;

    @Inject
    private SelfNodeProvider selfNodeProvider;


    @Inject
    private Persistence distributedStorage;

    @Inject
    private Persistence localStorage;

    @Subscribe
    public void handle(Find command) throws SocketException {
        if (command.isLocal() && command.getTerm() != null) {
            List<Bundle> byStringKey = localStorage.findByHash(Collections.singletonList(command.getHash()));
            mainBus.post(new UpdateResults(byStringKey));
        }

        if (!command.isLocal() && command.getTerm() != null) {
            try {
                ArrayList<byte[]> hashes = new ArrayList<>();
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String[] terms = splitter.split(command.getTerm());
                for (String term : terms) {
                    digest.update(term.getBytes("UTF-8"));
                    hashes.add(digest.digest());
                }
                distributedStorage.findByHash(hashes);
            } catch (NoSuchAlgorithmException e) {
                logger.error("SHA-256 not available");
                mainBus.post(e);
            } catch (UnsupportedEncodingException e) {
                logger.error("UTF-8 encoding not available on platform... (really?!)");
                mainBus.post(e);
            }
        }
    }

    @PostConstruct
    void register() {
        mainBus.register(this);
    }

}
