package userinterface.scenes.login;

import core.model.Account;
import core.service.LoginService;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import userinterface.LoginHandle;
import userinterface.scenes.BaseController;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends BaseController implements LoginService.Listener {

    @FXML private TextField usernameField;

    @FXML private PasswordField passwordField;

    @FXML Pane container;

    private LoginService model = new LoginService();

    @FXML private void loginButtonAction(ActionEvent event) {
        if(usernameField.getText().trim().length() > 0){
            if(passwordField.getText().trim().length() > 0){
                model.login(usernameField.getText(), passwordField.getText());
            } else onError("Password is blank.");
        } else onError("Username is blank.");
    }

    @Override public void initialize(URL location, ResourceBundle resources) {
//        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
//        container.prefWidthProperty().bind(property);
        model.setListener(this);

    }

    @Override public void onLoginSuccees(Account account) {
        LoginHandle.getInstance().logAccount(account);
    }

    @Override public void onLoginFailed(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override public void onError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
