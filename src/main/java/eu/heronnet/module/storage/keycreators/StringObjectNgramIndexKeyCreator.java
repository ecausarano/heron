package eu.heronnet.module.storage.keycreators;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryMultiKeyCreator;
import eu.heronnet.model.Bundle;
import eu.heronnet.model.Node;
import eu.heronnet.model.Statement;
import eu.heronnet.model.StringNode;
import eu.heronnet.module.storage.binding.BundleBinding;
import eu.heronnet.module.storage.util.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static eu.heronnet.model.NodeType.STRING;

/**
 * @author edoardocausarano
 */
@Component
public class StringObjectNgramIndexKeyCreator implements SecondaryMultiKeyCreator {

    private static final int MINIMUM_NGRAM_LENGTH = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(StringObjectNgramIndexKeyCreator.class);
    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s");

    @Inject
    BundleBinding bundleBinding;

    private static List<String> generateNgrams(String str, int minLength, int maxLength) {
        char[] chars = str.toLowerCase().toCharArray();

        int L = chars.length;
        int resultCount = (2 * L - maxLength - minLength + 2) * (maxLength - minLength + 1) / 2;

        String[] result = new String[resultCount];

        int resultOffset = 0;
        for (int current = minLength; current <= maxLength; current++) {
            for (int i = 0; i <= L - current; i++) {
                result[resultOffset++] = new String(chars, i, current);
            }
        }
        return Arrays.asList(result);
    }

    @Override
    public void createSecondaryKeys(SecondaryDatabase secondary, DatabaseEntry key, DatabaseEntry data, Set<DatabaseEntry> results) {
        Bundle bundle = bundleBinding.entryToObject(data);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            Set<Statement> statements = bundle.getStatements();
            statements.forEach(statement -> {
                Node object = statement.getObject();
                if (STRING.equals(object.getNodeType())) {
                    String[] tokens = tokenize(((StringNode) object).getData());
                    Arrays.stream(tokens).map(token -> generateNgrams(token, MINIMUM_NGRAM_LENGTH, token.length()))
                            .flatMap(Collection::stream).forEach(ngram -> {
                        digest.reset();
                        digest.update(ngram.getBytes());
                        byte[] bytes = digest.digest();
                        LOGGER.debug("Indexing nGram with digest [{}:{}]", ngram, HexUtil.bytesToHex(bytes));
                        results.add(new DatabaseEntry(bytes));
                    });
                }
            });
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error, SHA-256 not available on platform");
        }
    }

    private String[] tokenize(String string) {
        return SPACE_PATTERN.split(string);
    }

}
