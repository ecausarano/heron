package eu.heronnet.module.gui.fx.task;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import eu.heronnet.module.kad.net.SelfNodeProvider;
import eu.heronnet.module.storage.Persistence;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Simple implementation to search for N-grams separated by whitespace characters.
 *
 * @author edoardocausarano
 */
@Component
@Scope("prototype")
public class SearchByPredicate extends Task<Void> {

    private static final Logger logger = LoggerFactory.getLogger(SearchByPredicate.class);
    private static final Pattern splitter = Pattern.compile("\\s");

    @Inject
    private SelfNodeProvider selfNodeProvider;
    @Inject
    private Persistence clientImpl;

    private String query;

    public void setQuery(String string) {
        this.query = string;
    }

    @Override
    protected Void call() throws Exception {
        try {
            ArrayList<byte[]> hashes = new ArrayList<>();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String[] terms = splitter.split(query);
            for (String term : terms) {
                digest.update(term.getBytes("UTF-8"));
                hashes.add(digest.digest());
            }
            clientImpl.findByHash(hashes);
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256 not available");
        } catch (UnsupportedEncodingException e) {
            logger.error("UTF-8 encoding not available on platform... (really?!)");
        }
        return null;
    }

}
