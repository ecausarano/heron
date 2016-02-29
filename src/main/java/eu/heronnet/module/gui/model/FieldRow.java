package eu.heronnet.module.gui.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * @author edoardocausarano
 */
public class FieldRow {

    private final SimpleStringProperty name;
    private final SimpleStringProperty value;

    public FieldRow(final String name, final String value) {
        this.name = new SimpleStringProperty(name);
        this.value = new SimpleStringProperty(value);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }


    public SimpleStringProperty valueProperty() {
        return value;
    }
}
