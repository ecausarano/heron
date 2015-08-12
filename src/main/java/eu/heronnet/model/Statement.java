package eu.heronnet.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
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
}
