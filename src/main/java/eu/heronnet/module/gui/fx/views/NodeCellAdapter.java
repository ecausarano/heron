package eu.heronnet.module.gui.fx.views;

import eu.heronnet.model.DateNode;
import eu.heronnet.model.Node;
import eu.heronnet.model.StringNode;
import eu.heronnet.module.gui.model.FieldRow;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author edoardocausarano
 */
public class NodeCellAdapter extends TableCell<FieldRow, Node> {

    private static final Logger logger = LoggerFactory.getLogger(NodeCellAdapter.class);

    private Node item;

    @Override
    protected void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            this.item = item;
            logger.debug("adapting item={}", item.getNodeType());
            setText(item.toString());
            setGraphic(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }


    @Override
    public void startEdit() {
        super.startEdit();
        logger.debug("editing cell type={}", item.getNodeType());
        update(item);
    }

    @Override
    public void commitEdit(Node newValue) {
        super.commitEdit(newValue);
        item = newValue;
        commit(item);

    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        commit(item);
    }

    private void update(Node item) {
        if (item instanceof StringNode) {
            setText(null);
            TextField textField = new TextField((String) item.getData());
            setGraphic(textField);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } else if (item instanceof DateNode) {
            setText(null);
            Date data = ((DateNode) item).getData();
            SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
            TextField textField = new TextField(yyyy.format(data));
            setGraphic(textField);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

    private void commit(Node item) {
        if (item instanceof StringNode) {
            setText((String) item.getData());
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else if (item instanceof DateNode) {
            Date data = ((DateNode) item).getData();
            SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
            TextField textField = new TextField();
            setText(yyyy.format(data));
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }
}
