package eu.heronnet.model.builder;

import eu.heronnet.model.StringNode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author edoardocausarano
 */
public class StringNodeBuilder {

    private StringNodeBuilder() {
    }

    public static StringNode withString(String string) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        byte[] hash = digest.digest(string.getBytes());
        return new StringNode(hash, string);
    }
}
