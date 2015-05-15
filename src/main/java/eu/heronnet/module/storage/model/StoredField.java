package eu.heronnet.module.storage.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * @author edoardocausarano
 */
@Entity
public class StoredField {

    private static final int NGRAM_MIN = 2;
    @PrimaryKey
    private String hash;
    private String name;
    private String value;
    @SecondaryKey(relate = Relationship.MANY_TO_MANY)
    private Set<String> nGrams;

    public StoredField() {
    }

    public StoredField(String hash, String name, String value) {
        this.hash = hash;
        this.name = name;
        this.value = value;
        this.nGrams = StoredField.ngrams(value, NGRAM_MIN, value.length());
    }

    public static Set<String> ngrams(String str, int n, int m) {
        char[] chars = str.toLowerCase().toCharArray();

        int L = chars.length;
        int resultCount = (2 * L - m - n + 2) * (m - n + 1) / 2;

        String[] result = new String[resultCount];

        int resultOffset = 0;
        for (int current = n; current <= m; current++) {
            for (int i = 0; i <= L - current; i++) {
                result[resultOffset++] = new String(chars, i, current);
            }
        }
        return new HashSet<>(Arrays.asList(result));
    }

    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Set<String> getnGrams() {
        return nGrams;
    }

}
