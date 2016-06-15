package eu.heronnet.module.gui.fx.task;

import eu.heronnet.model.Bundle;
import eu.heronnet.module.storage.Persistence;
import eu.heronnet.module.storage.util.HexUtil;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    private List<String> query;
    private Boolean isLocal = Boolean.TRUE;
    private List<Bundle> bundles;
    private byte[] hash;

    public void setQuery(List<String> strings) {
        this.query = new ArrayList<>(strings);
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
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
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");

                    final List<byte[]> hashes = query.stream().flatMap(splitter::splitAsStream).map(term -> {
                        try {
                            digest.update(term.getBytes("UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            // ignore
                        }
                        return digest.digest();
                    }).collect(Collectors.toList());

                    // add specified hash
                    if (hash != null) {
                        hashes.add(hash);
                    }

                    if (isLocal) {
                        if (query.size() == 1 && query.get(0).equals("")) {
                            bundles = local.getAll();
                        } else {
                            bundles = local.findByHash(hashes);
                        }
                    } else {
                        bundles = distributed.findByHash(hashes);
                    }
                    return bundles;
                } catch (NoSuchAlgorithmException e) {
                    logger.error("SHA-256 not available");
                    return Collections.emptyList();
                }
            }

        };
    }

    @Override
    protected void succeeded() {
        logger.debug("Search request for hash=[{}], query=\"{}\", {} entries found", HexUtil.bytesToHex(hash), query, bundles.size());
    }
}
