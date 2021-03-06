package eu.heronnet.module.gui.model;

import eu.heronnet.model.Bundle;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author edoardocausarano
 */
class SearchResult {

    private final SimpleStringProperty title;
    private final SimpleStringProperty type;
    private Bundle bundle;

    public SearchResult(String title, String type) {
        this.title = new SimpleStringProperty(title);
        this.type = new SimpleStringProperty(type);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }
}
