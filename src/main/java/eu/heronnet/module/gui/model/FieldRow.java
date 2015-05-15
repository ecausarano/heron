package eu.heronnet.module.gui.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * @author edoardocausarano
 */
public class FieldRow {

    private final SimpleStringProperty name;
    private final SimpleStringProperty value;

    public FieldRow(String name, String value) {
        this.name = new SimpleStringProperty(name);
        this.value = new SimpleStringProperty(value);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }
}
