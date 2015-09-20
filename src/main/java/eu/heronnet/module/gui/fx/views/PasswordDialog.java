package eu.heronnet.module.gui.fx.views;

import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

/**
 * @author edoardocausarano
 */
public class PasswordDialog extends Dialog<Pair<String, String>> {

    public PasswordDialog(
            @NamedArg(value = "title", defaultValue = "Enter Password") String title,
            @NamedArg(value = "header") String headerText)
    {

        setTitle(title);
        setHeaderText(headerText);

//        // Set the icon (must be included in the project).
//        setGraphic(new ImageView(
//                this.getClass().getResource("login.png").toString()
//        ));

// Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

 // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Password:"), 0, 0);
        grid.add(password, 1, 0);

// Enable/Disable login button depending on whether a username was entered.
        Node loginButton = getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).

        password.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    loginButton.setDisable(newValue.trim().isEmpty());
                });

        getDialogPane().setContent(grid);
// Request focus on the username field by default.
        Platform.runLater(password::requestFocus);
    }

//
//// Convert the result to a username-password-pair when the login button is clicked.
//    dialog.setResultConverter(dialogButton->
//
//    {
//        if (dialogButton == loginButtonType) {
//            return new Pair<>(username.getText(), password.getText());
//        }
//        return null;
//    }
//
//    );
//
//    Optional<Pair<String, String>> result = dialog.showAndWait();
//
//    result.ifPresent(usernamePassword->
//
//    {
//        System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValues());
//    }
//
//    );

}
