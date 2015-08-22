package eu.heronnet.model.builder;

import eu.heronnet.model.Bundle;
import eu.heronnet.model.IdentifierNode;
import eu.heronnet.model.Statement;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by edo on 07/08/15.
 */
public class BundleBuilder {

    private static final Bundle EMPTY_BUNDLE = new Bundle(new byte[32], IdentifierNode.anyId(), Collections.emptySet());

    private IdentifierNode subject;
    private Set<Statement> statements = new HashSet<>();
    private byte[] id;

    public BundleBuilder() {
    }

    /**
     * @return an immutable empty {@link Bundle}
     */
    public static Bundle emptyBundle() {
        return EMPTY_BUNDLE;
    }

    public BundleBuilder withId(byte[] id) {
        this.id = id;
        return this;
    }

    public BundleBuilder withSubject(IdentifierNode subject) {
        this.subject = subject;
        return this;
    }

    public BundleBuilder withStatement(Statement statement) {
        statements.add(statement);
        return this;
    }

    /**
     *
     * @return the built {@link Bundle}
     */
    public Bundle build() throws NoSuchAlgorithmException {
        if (subject == null)
            throw new IllegalStateException("cannot build bundle if subject is undefined");
        if (statements.isEmpty())
            throw new IllegalStateException("cannot build a bundle with no statements");

        if (id == null) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(subject.getNodeId());
            statements.forEach(statement -> {
                digest.update(statement.getPredicate().getNodeId());
                digest.update(statement.getObject().getNodeId());
            });
            id = digest.digest();
        }

        return new Bundle(id, subject, statements);
    }

}
