package eu.heronnet.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An "RDF" item that conveys an item of information regarding an {@link IdentifierNode}
 * Can be thought of as a graph edge
 *
 * Created by edo on 07/08/15.
 */
public class Statement {

    private final StringNode predicate;

    private final Node object;

    public Statement(@JsonProperty("predicate") StringNode predicate, @JsonProperty("object") Node object) {
        this.predicate = predicate;
        this.object = object;
    }

    public StringNode getPredicate() {
        return predicate;
    }

    public Node getObject() {
        return object;
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
