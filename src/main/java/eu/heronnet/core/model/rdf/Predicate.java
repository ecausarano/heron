package eu.heronnet.core.model.rdf;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by edoardocausarano on 12-09-14.
 */
public class Predicate {

    public static Predicate ISA = new Predicate("isa");
    public static Predicate HAS = new Predicate("has");
    public static Predicate DESCRIBES = new Predicate("describes");
    public static Predicate SIGNED_BY = new Predicate("signedBy");

    public static Predicate[] BUILT_IN = {ISA, HAS, DESCRIBES, SIGNED_BY};
    private String predicate;

    @JsonCreator
    public Predicate(@JsonProperty("predicate") String predicate) {
        this.predicate = predicate;
    }

    public String getPredicate() {
        return predicate;
    }

    @Override
    public String toString() {
        return predicate;
    }

}
