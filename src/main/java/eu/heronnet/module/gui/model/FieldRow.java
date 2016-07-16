package eu.heronnet.module.gui.model;

import eu.heronnet.model.IRI;
import eu.heronnet.model.Node;
import eu.heronnet.model.Statement;
import javafx.beans.property.adapter.JavaBeanObjectProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.scene.control.TableRow;

/**
 * Adapter class to display {@link Statement} domain objects in a JavaFX {@link TableRow}
 *
 * @author edoardocausarano
 */
public class FieldRow {

    private final Statement statement;

    private final JavaBeanObjectProperty<IRI> name;
    private final JavaBeanObjectProperty<Node> value;

    public FieldRow(Statement statement) {
        this.statement = statement;
        try {
            JavaBeanObjectPropertyBuilder<IRI> predicateBuilder = new JavaBeanObjectPropertyBuilder<>();
            predicateBuilder.bean(statement).name("predicate");
            this.name = predicateBuilder.build();

            JavaBeanObjectPropertyBuilder<Node> valueBuilder = new JavaBeanObjectPropertyBuilder<>();
            valueBuilder.bean(statement).name("object");
            this.value = valueBuilder.build();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Statement getStatement() {
        return statement;
    }

    public IRI getName() {
        return name.get();
    }

    public JavaBeanObjectProperty<IRI> nameProperty() {
        return name;
    }

    public void setName(IRI name) {
        this.name.set(name);
    }

    public Node getValue() {
        return value.get();
    }

    public JavaBeanObjectProperty<Node> valueProperty() {
        return value;
    }

    public void setValue(Node value) {
        this.value.set(value);
    }
}
