package eu.heronnet.module.storage.keycreators;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryMultiKeyCreator;

import eu.heronnet.core.model.Triple;
import eu.heronnet.module.storage.binding.StringTripleBinding;
import eu.heronnet.module.storage.util.HexUtil;

/**
 * @author edoardocausarano
 */
public class StringNGramKeyCreator implements SecondaryMultiKeyCreator {

    private static final int MINIMUM_NGRAM_LENGTH = 3;
    private static final int MAXIMUM_NGRAM_LENGTH = 7;
    private static final Logger LOGGER = LoggerFactory.getLogger(StringNGramKeyCreator.class);
    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s");

    @Inject
    StringTripleBinding tripleBinding;

    private static Set<String> generateNgrams(String str, int minLength, int maxLength) {
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
        return new HashSet<>(Arrays.asList(result));
    }

    @Override
    public void createSecondaryKeys(SecondaryDatabase secondary, DatabaseEntry key, DatabaseEntry data, Set<DatabaseEntry> results) {
        Triple<String> triple = tripleBinding.entryToObject(data);
        try {
            String tripleObject = triple.getStatement().getObject();
            String[] tokens = tokenize(tripleObject);

            for (String token : tokens) {
                Set<String> nGrams = generateNgrams(token, MINIMUM_NGRAM_LENGTH, token.length());

                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                for (String nGram : nGrams) {
                    digest.reset();
                    digest.update(nGram.getBytes());
                    byte[] bytes = digest.digest();
                    LOGGER.debug("Indexing nGram with digest [{}:{}]", nGram, HexUtil.bytesToHex(bytes));
                    results.add(new DatabaseEntry(bytes));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error, SHA-256 not available on platform");
        }
    }

    private String[] tokenize(String string) {
        return SPACE_PATTERN.split(string);
    }

}
