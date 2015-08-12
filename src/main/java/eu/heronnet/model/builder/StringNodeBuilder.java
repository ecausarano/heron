package eu.heronnet.model.builder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import eu.heronnet.model.StringNode;

/**
 * @author edoardocausarano
 */
public class StringNodeBuilder {

    private StringNodeBuilder() {
    }

    public static final StringNode withString(String string) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        byte[] hash = digest.digest(string.getBytes());
        return new StringNode(hash, string);
    }
}
