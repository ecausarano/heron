package eu.heronnet.core.model;

/**
 * @author edoardocausarano
 */
public class Statement<T> {

    T object;
    private String predicate;

    private Statement() {
    }

    public Statement(String predicate, T object) {
        this.predicate = predicate;
        this.object = object;
    }

    public String getPredicate() {
        return predicate;
    }

    public T getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "Statement{" + "predicate='" + predicate + '\'' + ", object=" + object + '}';
    }
}
