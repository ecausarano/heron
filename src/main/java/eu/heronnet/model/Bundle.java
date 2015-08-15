package eu.heronnet.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An "RDF" collection of {@link Statement statements} relative to an {@link IdentifierNode subject}
 * Indeed an RDF triple is a degenerate {@code Bundle} containing one {@code statement}
 *
 * Created by edo on 07/08/15.
 */
public class Bundle {

    private final IdentifierNode subject;

    private final HashSet<Statement> statements = new HashSet<>();

    public Bundle(IdentifierNode subject) {
        this.subject = subject;
    }

    public Bundle(@JsonProperty("subject") IdentifierNode subject, @JsonProperty("statements") Set<Statement> statements) {
        this.subject = subject;
        this.statements.addAll(statements);
    }

    public void add(Statement statement) {
        statements.add(statement);
    }

    public IdentifierNode getSubject() {
        return subject;
    }

    public Set<Statement> getStatements() {
        return new HashSet<>(statements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bundle bundle = (Bundle) o;

        return subject.equals(bundle.subject) && statements.equals(bundle.statements);

    }

    @Override
    public int hashCode() {
        int result = subject.hashCode();
        result = 31 * result + statements.hashCode();
        return result;
    }
}
