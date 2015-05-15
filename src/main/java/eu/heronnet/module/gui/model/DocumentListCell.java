package eu.heronnet.module.gui.model;

import java.util.Set;

import javafx.scene.control.ListCell;

import eu.heronnet.core.model.Document;
import eu.heronnet.core.model.Field;

/**
 * @author edoardocausarano
 */
public class DocumentListCell extends ListCell<Document> {
    @Override
    protected void updateItem(Document item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        }
        else {
            Set<Field> fields = item.getMeta();
            StringBuilder builder = new StringBuilder();
            for (Field field : fields) {
                builder.append(field.getName()).append("=").append(field.getValue()).append(" ");
            }
            setText(builder.toString());
        }
    }
}
