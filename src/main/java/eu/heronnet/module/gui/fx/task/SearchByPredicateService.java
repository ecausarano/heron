package eu.heronnet.module.gui.fx.task;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import eu.heronnet.model.Bundle;
import eu.heronnet.module.storage.Persistence;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Simple implementation to search for N-grams separated by whitespace characters.
 *
 * @author edoardocausarano
 */
public class SearchByPredicateService extends Service<List<Bundle>> {

    private static final Logger logger = LoggerFactory.getLogger(SearchByPredicateService.class);
    private static final Pattern splitter = Pattern.compile("\\s");

    @Inject
    @Qualifier(value = "distributedStorage")
    private Persistence distributed;
    @Inject
    @Qualifier(value = "localStorage")
    private Persistence local;

    private String query;
    private Boolean isLocal = Boolean.TRUE;
    private List<Bundle> bundles;

    public void setQuery(String string) {
        this.query = string;
    }

    public void setLocal(Boolean isLocal) {
        this.isLocal = isLocal;
    }
    @Override
    protected Task<List<Bundle>> createTask() {
        return new Task<List<Bundle>>() {
            @Override
            protected List<Bundle> call() throws Exception {
                try {
                    ArrayList<byte[]> hashes = new ArrayList<>();
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    String[] terms = splitter.split(query);

                    for (String term : terms) {
                        digest.update(term.getBytes("UTF-8"));
                        hashes.add(digest.digest());
                    }

                    if (isLocal) {
                        if ("".equals(query)) {
                            bundles = local.getAll();
                        } else {
                            bundles = local.findByHash(hashes);
                        }
                    } else {
                        bundles = distributed.findByHash(hashes);
                    }
                } catch (NoSuchAlgorithmException e) {
                    logger.error("SHA-256 not available");
                } catch (UnsupportedEncodingException e) {
                    logger.error("UTF-8 encoding not available on platform... (really?!)");
                }
                return bundles;
            }

        };
    }

    @Override
    protected void succeeded() {
        logger.debug("Initiated search request for \"{}\", {} entries found", query, bundles.size());
    }
}
