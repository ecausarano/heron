package eu.heronnet.model;

/**
 * An "RDF" item that conveys an item of information regarding an {@link IdentifierNode}
 * Can be thought of as a graph edge
 *
 * Created by edo on 07/08/15.
 */
public class Statement {

    private IRI predicate;

    private Node object;

    public Statement(
            IRI predicate,
            Node object)
    {
        this.predicate = predicate;
        this.object = object;
    }

    public IRI getPredicate() {
        return predicate;
    }

    public void setPredicate(IRI predicate) {
        this.predicate = predicate;
    }

    public Node getObject() {
        return object;
    }

    public void setObject(Node object) {
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Statement statement = (Statement) o;

        return predicate.equals(statement.predicate) && object.equals(statement.object);

    }

    @Override
    public int hashCode() {
        int result = predicate.hashCode();
        result = 31 * result + object.hashCode();
        return result;
    }
}
