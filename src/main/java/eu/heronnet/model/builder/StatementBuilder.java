package eu.heronnet.model.builder;

import eu.heronnet.model.Statement;

import java.security.NoSuchAlgorithmException;

/**
 * @author edoardocausarano
 */
public class StatementBuilder {

    public static Statement pgpSignature(String signature) throws NoSuchAlgorithmException {
        return new Statement(
                StringNodeBuilder.withString("http://xmlns.com/wot/0.1/assurance"),
                StringNodeBuilder.withString(signature));
    }
}
