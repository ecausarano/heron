package eu.heronnet.module.gui.model;

import eu.heronnet.model.BinaryDataNode;
import eu.heronnet.model.Bundle;
import eu.heronnet.model.Node;
import eu.heronnet.model.Statement;
import javafx.scene.control.ListCell;

import java.util.Set;

/**
 * @author edoardocausarano
 */
public class DocumentListCell extends ListCell<Bundle> {
    @Override
    protected void updateItem(Bundle item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        }
        else {
            Set<Statement> statements = item.getStatements();
            StringBuilder builder = new StringBuilder();
            for (Statement statement : statements) {
                // TODO - add adaptors
                Node object = statement.getObject();
                if (!(object instanceof BinaryDataNode)) {
                    builder.append(statement.getPredicate()).append("=").append(object).append(" ").append("\n");
                }
            }
            setText(builder.toString());
        }
    }


}
