package eu.heronnet.model.builder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import eu.heronnet.model.IRI;

/**
 * @author edoardocausarano
 */
public class IRIBuilder {

    private IRIBuilder() {}

    /**
     * Builds a {@link IRI} with the given absolute IRI
     *
     * @param string the absolute IRI
     * @return a {@link IRI} for the given value
     * @throws RuntimeException if SHA-256 message digest algorithm is not available on the platform
     */
    public static IRI withString(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return new IRI(digest.digest(string.getBytes()), string);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
