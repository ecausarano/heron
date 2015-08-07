package eu.heronnet.model.builder;

import eu.heronnet.model.Bundle;
import eu.heronnet.model.IdentifierNode;
import eu.heronnet.model.Statement;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by edo on 07/08/15.
 */
public class BundleBuilder {

    private static final Bundle EMPTY_BUNDLE = new Bundle(null, Collections.emptySet());

    private IdentifierNode subject;
    private Set<Statement> statements = new HashSet<>();

    public BundleBuilder() {
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
    public Bundle build() {
        if (subject == null)
            throw new IllegalStateException("cannot build bundle if subject is undefined");
        if (statements.isEmpty())
            throw new IllegalStateException("cannot build a bundle with no statements");
        return new Bundle(subject, statements);
    }

    /**
     * @return an immutable empty {@link Bundle}
     */
    public static Bundle emptyBundle() {
        return  EMPTY_BUNDLE;
    }

}
