package userinterface.scenes.administrator;

import core.model.Account;
import core.model.AccountType;
import core.model.Model;
import core.model.StaffInformation;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import userinterface.scenes.BaseController;

import javax.swing.text.DateFormatter;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class StaffDetailsController extends BaseController {

    public static final String STAFF_PARAMETER_NAME = "staff";

    @FXML Pane container;

    @FXML Label staffNumberLabel;
    @FXML Label nameLabel;
    @FXML Label genderLabel;
    @FXML Label ageLabel;
    @FXML Label birthdateLabel;
    @FXML Label positionLabel;
    @FXML Label expertiseLabel;
    @FXML Label contactNumberLabel;
    @FXML Label emailLabel;
    @FXML Label addressLabel;
    @FXML Label usernameLabel;

    @FXML ChoiceBox<AccountType> accountTypeChoice;

    @FXML RadioButton activateRadio;
    @FXML RadioButton deactivateRadio;


    StaffInformation staffInformation;
    Account account;

    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        super.initialize(location, bundle);
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        container.prefWidthProperty().bind(property);
        accountTypeChoice.setItems(FXCollections.observableArrayList(AccountType.values()));
    }

    @Override
    protected void onParametersReceived() {
        super.onParametersReceived();
        staffInformation = (StaffInformation)parameters.get(STAFF_PARAMETER_NAME);
        initializeStaffBindings();
        account = staffInformation.getAccount();
        initializeAccountBindings();
    }

    private void initializeStaffBindings() {
        staffNumberLabel.textProperty().bind(staffInformation.idProperty().asString());
        nameLabel.textProperty().bind(staffInformation.fullnameProperty());
        genderLabel.textProperty().bind(staffInformation.genderProperty().asString());
        ageLabel.textProperty().bind(staffInformation.ageProperty().asString());
        birthdateLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                LocalDate birthdate = staffInformation.getBirthDate();
                if(birthdate == null) return null;
                DateTimeFormatter format = DateTimeFormatter.ofPattern("LLLL dd yyyy");
                return birthdate.format(format);
            }
        }));

        positionLabel.textProperty().bind(staffInformation.positionProperty());
        expertiseLabel.textProperty().bind(staffInformation.expertiseProperty());
        contactNumberLabel.textProperty().bind(staffInformation.contactNumberProperty());
        emailLabel.textProperty().bind(staffInformation.emailProperty());
        addressLabel.textProperty().bind(staffInformation.addressProperty());
    }

    private void initializeAccountBindings() {
        usernameLabel.textProperty().bind(account.usernameProperty());
        boolean isActive = account.isActive();
        accountTypeChoice.setValue(account.getAccountType());
        activateRadio.setSelected(isActive);
        deactivateRadio.setSelected(!isActive);
        account.setErrorListener(errorListener);
        accountTypeChoice.setOnAction(this::accountTypeChoiceAction);
    }

    @FXML private void accountTypeChoiceAction(ActionEvent event){
        AccountType type = accountTypeChoice.getValue();
        if(type == AccountType.None){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Account");
            alert.setContentText("Account type cant be set to None.");
            alert.showAndWait();
            return;
        }
        account.beginEdit();
        account.setAccountType(type);
        account.applyEdit();
    }

    @FXML private void activate(ActionEvent event){
        account.activate();
    }

    @FXML private void deactivate(ActionEvent event){
        account.deactivate();
    }

    private final Model.ErrorListener errorListener = new Model.ErrorListener() {
        @Override
        public void onError(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("An error occurred");
            alert.setContentText(message);
            alert.showAndWait();
        }
    };


}
