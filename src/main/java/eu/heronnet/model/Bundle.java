package eu.heronnet.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.heronnet.module.storage.util.HexUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * An "RDF" collection of {@link Statement statements} relative to an {@link IdentifierNode subject}
 * Indeed an RDF triple is a degenerate {@code Bundle} containing one {@code statement}
 *
 * Created by edo on 07/08/15.
 */
public class Bundle extends Node<Set<Statement>> {

    private final IdentifierNode subject;

    private final HashSet<Statement> statements = new HashSet<>();

    public Bundle(@JsonProperty("nodeId") byte[] nodeId, @JsonProperty("subject") IdentifierNode subject, @JsonProperty("data") Set<Statement> statements) {
        super(nodeId, NodeType.BUNDLE);
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
    public Set<Statement> getData() {
        return getStatements();
    }

    @Override
    public String toString() {
        return HexUtil.bytesToHex(nodeId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bundle bundle = (Bundle) o;

        return subject.equals(bundle.subject) && statements.equals(bundle.statements);

    }
}
