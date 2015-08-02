package eu.heronnet.module.gui.model;

import java.util.Set;

import javafx.scene.control.ListCell;

import eu.heronnet.core.model.BinaryStatement;
import eu.heronnet.core.model.Bundle;
import eu.heronnet.core.model.Statement;

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
                if (!(statement instanceof BinaryStatement)) {
                    builder.append(statement.getPredicate()).append("=").append(statement.getObject()).append(" ").append("\n");
                }
            }
            setText(builder.toString());
        }
    }
}
