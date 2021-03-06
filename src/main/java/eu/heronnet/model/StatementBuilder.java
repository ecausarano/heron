package eu.heronnet.model;

import eu.heronnet.model.vocabulary.HRN;

import java.security.NoSuchAlgorithmException;

/**
 * Builder for {@link Statement Statements}
 *
 * @author edoardocausarano
 */
public class StatementBuilder {

    /**
     * Static utility method to return a PGP Signature statement
     *
     * @param signature  the PGP signature
     * @return  the {@link Statement} with the PGP signature predicate
     * @throws NoSuchAlgorithmException
     */
    public static Statement pgpSignature(String signature) throws NoSuchAlgorithmException {
        return new Statement(HRN.SIGNATURE.getIri(), StringNodeBuilder.withString(signature));
    }
}
