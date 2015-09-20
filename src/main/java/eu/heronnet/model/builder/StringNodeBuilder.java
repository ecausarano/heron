package eu.heronnet.model.builder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import eu.heronnet.model.StringNode;

/**
 * @author edoardocausarano
 */
public class StringNodeBuilder {

    private StringNodeBuilder() {}

    /**
     * Builds a {@link StringNode} with the given literal value
     *
     * @param string  the value to represent
     * @return  a {@link StringNode} for the given value
     * @throws RuntimeException if SHA-256 message digest algorithm is not available on the platform
     */
    public static StringNode withString(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return new StringNode(digest.digest(string.getBytes()), string);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
