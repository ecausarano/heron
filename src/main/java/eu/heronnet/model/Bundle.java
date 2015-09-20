package eu.heronnet.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import eu.heronnet.module.storage.util.HexUtil;

/**
 * An "RDF" collection of {@link Statement statements} relative to an {@link IdentifierNode subject}
 * Indeed an RDF triple is a degenerate {@code Bundle} containing one {@code statement}
 *
 * Created by edo on 07/08/15.
 */
public class Bundle extends Node<Set<Statement>> {

    private final IdentifierNode subject;

    private final Set<Statement> statements;

    public Bundle(
            byte[] nodeId,
            IdentifierNode subject,
            Set<Statement> statements)
    {
        super(nodeId, NodeType.BUNDLE);
        this.subject = subject;
        this.statements = Collections.unmodifiableSet(new HashSet<>(statements));
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
