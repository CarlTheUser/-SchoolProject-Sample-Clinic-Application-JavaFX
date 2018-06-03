package userinterface.prompt;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmationPrompt extends Prompt<Boolean> {

    public ConfirmationPrompt(Stage parent) {
        super(parent);
    }

    @Override
    public Boolean getResponse(String promptTitle, String promptBody) {
        boolean confirm = false;

        Stage dialogStage = new Stage();
        dialogStage.initOwner(parent);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.showAndWait();


        return confirm;
    }


    private class Controller implements Initializable{

        boolean confirm = false;

        @FXML Label promptMessageLabel;

        final String promptMessage;

        public Controller(String prompt) {
            promptMessage = prompt;
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            promptMessageLabel.setText(promptMessage);
        }

        @FXML private void confirmationAction(ActionEvent event){
            confirm = true;
        }

        @FXML private void cancellationAction(ActionEvent event){
            confirm = true;
        }
    }
}