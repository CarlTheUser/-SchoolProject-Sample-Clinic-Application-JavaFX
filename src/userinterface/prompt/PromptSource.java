package userinterface.prompt;

import javafx.scene.control.Alert;

public interface PromptSource {
    Alert createAlertDialog(Alert.AlertType alertType, String promptTitle, String promptMessage);
}
