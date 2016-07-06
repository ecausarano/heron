package eu.heronnet.module.gui.model;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import eu.heronnet.model.*;
import eu.heronnet.model.vocabulary.DC;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Set;

/**
 * @author edoardocausarano
 */
public class DocumentListCell extends ListCell<Bundle> {

    private static final String CACHE_LIST_ICON_CLASS = "cache-list-icon";
    private static final String FONT_AWESOME = "FontAwesome";

    private GridPane grid = new GridPane();
    private FontAwesomeIconView icon = new FontAwesomeIconView();
    private Label title = new Label();
    private Label authors = new Label();
    private Label publication = new Label();

    public DocumentListCell() {
        configureGrid();
//        configureIcon();
        configureTitle();
        configureAuthors();
        addControlsToGrid();
    }

    private void configureGrid() {
        grid.setHgap(10);
        grid.setVgap(4);
        grid.setPadding(new Insets(0, 10, 0, 10));
    }

    private void configureTitle() {
        title.setFont(Font.font(null, FontWeight.BOLD, 12));
    }
    private void configureAuthors() {
        authors.setFont(Font.font(null, FontWeight.LIGHT, 10));
    }

//    private void configureIcon() {
//        icon.setFont(Font.font(FONT_AWESOME, FontWeight.BOLD, 24));
//        icon.getStyleClass().add(CACHE_LIST_ICON_CLASS);
//    }

//    private void configureName() {
//        name.getStyleClass().add(CACHE_LIST_NAME_CLASS);
//    }
//
//    private void configureDifficultyTerrain() {
//        dt.getStyleClass().add(CACHE_LIST_DT_CLASS);
//    }

    private void addControlsToGrid() {
        grid.add(icon, 0, 0, 1, 2);
        grid.add(title, 1, 0);
        grid.add(authors, 1, 1);
    }


    @Override
    protected void updateItem(Bundle item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        }
        else {
            setText(null);
            Set<Statement> statements = item.getStatements();
            for (Statement statement : statements) {
                // TODO - add adaptors
                Node object = statement.getObject();
                if (!(object instanceof BinaryDataNode)) {
                    IRI predicate = statement.getPredicate();
                    if (DC.TYPE.equals(predicate)) {
                        // TODO set icon
                        icon.setIcon(FontAwesomeIcon.FILE_PDF_ALT);
                    } else if (DC.TITLE.equals(predicate)) {
                        title.setText(statement.getObject().getData().toString());
                    } else if (DC.CREATOR.equals(predicate)) {
                        authors.setText(statement.getObject().getData().toString());
                    }
                }
            }
            setGraphic(grid);
        }
    }


}
