package eu.heronnet.model.builder;

import eu.heronnet.model.Bundle;
import eu.heronnet.model.IdentifierNode;
import eu.heronnet.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Builder for bundles
 *
 * Created by edo on 07/08/15.
 */
public class BundleBuilder {

    private static final Logger logger = LoggerFactory.getLogger(BundleBuilder.class);

    private static final Bundle EMPTY_BUNDLE = new Bundle(new byte[32], IdentifierNode.anyId(), Collections.emptySet());

    private IdentifierNode subject;
    private final Set<Statement> statements = new HashSet<>();
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
     * Call to create a new immutable {@link Bundle}
     *
     * @return the built {@link Bundle}
     * @throws  RuntimeException if the SHA-256 hashing algorithm is not found on the system
     */
    public Bundle build() {
        if (subject == null)
            throw new IllegalStateException("cannot build bundle if subject is undefined");
        if (statements.isEmpty())
            throw new IllegalStateException("cannot build a bundle with no statements");

        if (id == null) {
            try {
                final MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.update(subject.getNodeId());
                statements.forEach(statement -> {
                    digest.update(statement.getPredicate().getNodeId());
                    digest.update(statement.getObject().getNodeId());
                });
                id = digest.digest();
            } catch (NoSuchAlgorithmException e) {
                logger.error("Could not locate SHA-256 hash, this should never happen");
                throw new RuntimeException(e);
            }
        }

        return new Bundle(id, subject, statements);
    }

}
