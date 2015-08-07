package eu.heronnet.model;

import javax.swing.plaf.nimbus.State;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by edo on 07/08/15.
 */
public class Bundle {

    private final IdentifierNode subject;

    private final HashSet<Statement> statements = new HashSet<>();

    public Bundle(IdentifierNode subject) {
        this.subject = subject;
    }

    public Bundle(IdentifierNode subject, Set<Statement> statements) {
        this.subject = subject;
        statements.addAll(statements);
    }

    public void add(Statement statement) {
        statements.add(statement);
    }

    public IdentifierNode getSubject() {
        return subject;
    }

    public Set<Statement> getStatements() {
        return new HashSet(statements);
    }
}
