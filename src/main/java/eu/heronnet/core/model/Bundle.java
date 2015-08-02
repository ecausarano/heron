package eu.heronnet.core.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import eu.heronnet.module.storage.util.HexUtil;

/**
 * An immutable object consisting of a {@code subject} (the SHA-256 hash of the
 * publication) and a {@link Set} of {@link Statement Statements} related to it
 *
 * @author edoardocausarano
 */
public class Bundle {

    // TODO - maybe make byte the subject 0?
    private static final Bundle EMPTY_BUNDLE = new Bundle(null, Collections.emptySet());

    private byte[] subject ;
    private Set<Statement> statements;

    private Bundle() {
    }
    /**
     *
     * @param statements  the statements to store in this bundle
     */
    public Bundle(final byte[] subject, final Set<Statement> statements) {
        this.subject = subject;
        this.statements = new HashSet<>(statements);
    }

    /**
     * @return an immutable empty {@link Bundle}
     */
    public static Bundle emptyBundle() {
        return  EMPTY_BUNDLE;
    }

    /**
     *
     * @return a {@link eu.heronnet.core.model.Bundle.BundleBuilder}
     */
    public static BundleBuilder builder() {
        return new BundleBuilder();
    }

    /**
     *
     * @return  the SHA-256 identifier of this bundle, ie the subject of all statements contained
     */
    public byte[] getSubject() {
        return subject;
    }

    /**
     *
     * @return an immutable set of the contained {@link Statement Statements}
     */
    public Set<Statement> getStatements() {
        return Collections.unmodifiableSet(statements);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Bundle{");
        sb.append("subject=");
        if (subject == null) sb.append("null");
        else {
            sb.append('[');
            sb.append(HexUtil.bytesToHex(subject));
            sb.append(']');
        }
        sb.append(", statements=").append(statements);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Builder for {@link Bundle}
     */
    public static class BundleBuilder {

        private byte[] subject;
        private Set<Statement> statements = new HashSet<>();

        public BundleBuilder() {
        }

        public BundleBuilder withSubject(byte[] subject) {
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
    }
}
