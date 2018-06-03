package userinterface.scenes.administrator;

import core.model.*;
import core.service.StaffService;
import core.util.NumberUtility;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import userinterface.URLSource;
import userinterface.customviewelement.ButtonTableCell;
import userinterface.navigation.UserNavigation;
import userinterface.scenes.BaseController;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.AbstractList;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;

public class StaffListController extends BaseController {

    @FXML Pane container;

    @FXML Button newStaffButton;

    @FXML Pane newStaffForm;

    @FXML TextField staffIdText;
    @FXML TextField firstnameText;
    @FXML TextField middlenameText;
    @FXML TextField lastnameText;
    @FXML ChoiceBox<Gender> genderChoice;
    @FXML DatePicker birthdatePicker;
    @FXML TextField positionText;
    @FXML TextField expertiseText;
    @FXML TextField contactNumberText;
    @FXML TextField emailText;
    @FXML TextField addressText;

    @FXML TextField usernameText;
    @FXML ChoiceBox<AccountType> accountTypeChoice;
    @FXML PasswordField passwordText;
    @FXML PasswordField confirmPasswordText;

    @FXML TableView<StaffInformation> staffTable;
    @FXML TableColumn<StaffInformation, Integer> idColumn;
    @FXML TableColumn<StaffInformation, String> staffColumn;
    @FXML TableColumn<StaffInformation, String> positionColumn;
    @FXML TableColumn<StaffInformation, String> expertiseColumn;
    @FXML TableColumn<StaffInformation, Button> actionColumn;

    StaffInformation newStaff;
    Account newAccount;

    final SimpleBooleanProperty isNewAccount = new SimpleBooleanProperty(this, "isNewAccount", false);

    StaffService staffService = new StaffService();
    ObservableList<StaffInformation> staffInformations = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        super.initialize(location, bundle);
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        container.prefWidthProperty().bind(property);
        initializeViewComponents();
    }

    private void initializeViewComponents() {
        staffService.setErrorListener(serviceError);
        BooleanExpression notNewAccount = isNewAccount.not();
        newStaffButton.visibleProperty().bind(notNewAccount);
        newStaffButton.managedProperty().bind(newStaffButton.visibleProperty());
        newStaffForm.visibleProperty().bind(isNewAccount);
        newStaffForm.managedProperty().bind(newStaffForm.visibleProperty());
        genderChoice.setItems(FXCollections.observableArrayList(Gender.values()));
        accountTypeChoice.setItems(FXCollections.observableArrayList(AccountType.values()));
        initializeTableBindings();
    }

    private void initializeTableBindings(){
        idColumn.setCellValueFactory(new PropertyValueFactory<StaffInformation, Integer>("id"));
        staffColumn.setCellValueFactory(new PropertyValueFactory<StaffInformation, String >("fullname"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<StaffInformation, String>("position"));
        expertiseColumn.setCellValueFactory(new PropertyValueFactory<StaffInformation, String>("expertise"));
        actionColumn.setCellFactory(ButtonTableCell.<StaffInformation>forTableColumn("view", new Function<StaffInformation, StaffInformation>() {
            @Override
            public StaffInformation apply(StaffInformation staffInformation) {
                Map<String, Object> passedParammeters = new Hashtable<>();
                passedParammeters.put(StaffDetailsController.STAFF_PARAMETER_NAME, staffInformation);
                URL url = URLSource.getURL("scenes/administrator/staffdetails.fxml");
                getMainView().getUserNavigation().navigate(new UserNavigation.NavigationItem(url, false, passedParammeters));
                return staffInformation;
            }
        }));
        staffInformations.addAll(staffService.getAllStaff());
        staffTable.setItems(staffInformations);
    }

    @FXML private void newStaffAction(ActionEvent event){
        initializeNewRecords();
        isNewAccount.setValue(true);
    }

    @FXML private void saveNewStaffAction(ActionEvent event){
        if(passwordText.getText().length() > 0){
            if(passwordText.getText().equals(confirmPasswordText.getText())){
                newAccount.setPassword(passwordText.getText());
                newAccount.save();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation error");
                alert.setContentText("Passwords doesnt match.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation error");
            alert.setContentText("Password can't be blank.");
            alert.showAndWait();
        }


    }

    @FXML private void cancelNewStaffAction(ActionEvent event){
        isNewAccount.setValue(false);
    }

    private void initializeNewRecords(){
        newAccount = Account.NewInstance(saveListener, validationListener, errorListener);
        newStaff = StaffInformation.NewInstance(saveListener, validationListener, errorListener);
        newAccount.setStaffInformation(newStaff);
        newAccount.idProperty().bind(newStaff.idProperty());
        staffIdText.textProperty().bindBidirectional(newStaff.idProperty(), new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return new DecimalFormat("#").format(object);
            }

            @Override
            public Number fromString(String string) {
                if(string.trim().length() == 0) return 0;
                if(NumberUtility.isNumber(string)){
                    return Long.parseLong(string);
                }
                return null;
            }
        });
        firstnameText.textProperty().bindBidirectional(newStaff.firstnameProperty());
        middlenameText.textProperty().bindBidirectional(newStaff.middlenameProperty());
        lastnameText.textProperty().bindBidirectional(newStaff.lastnameProperty());
        genderChoice.valueProperty().bindBidirectional(newStaff.genderProperty());
        birthdatePicker.valueProperty().bindBidirectional(newStaff.birthDateProperty());
        positionText.textProperty().bindBidirectional(newStaff.positionProperty());
        expertiseText.textProperty().bindBidirectional(newStaff.expertiseProperty());
        contactNumberText.textProperty().bindBidirectional(newStaff.contactNumberProperty());
        emailText.textProperty().bindBidirectional(newStaff.emailProperty());
        addressText.textProperty().bindBidirectional(newStaff.addressProperty());
        usernameText.textProperty().bindBidirectional(newAccount.usernameProperty());
        accountTypeChoice.valueProperty().bindBidirectional(newAccount.accountTypeProperty());
    }

    private void cleanupNewRecords(){
        if(newAccount != null){
            newAccount.setSaveListener(null);
            newAccount.setValidationListener(null);
            newAccount.setErrorListener(null);
        }
        if(newStaff != null){
            newStaff.setSaveListener(null);
            newStaff.setValidationListener(null);
            newStaff.setErrorListener(null);
        }
        assert newStaff != null;
        staffIdText.textProperty().unbindBidirectional(newStaff.idProperty());
        firstnameText.textProperty().unbindBidirectional(newStaff.firstnameProperty());
        middlenameText.textProperty().unbindBidirectional(newStaff.middlenameProperty());
        lastnameText.textProperty().unbindBidirectional(newStaff.lastnameProperty());
        genderChoice.valueProperty().unbindBidirectional(newStaff.genderProperty());
        birthdatePicker.valueProperty().unbindBidirectional(newStaff.birthDateProperty());
        positionText.textProperty().unbindBidirectional(newStaff.positionProperty());
        expertiseText.textProperty().unbindBidirectional(newStaff.expertiseProperty());
        contactNumberText.textProperty().unbindBidirectional(newStaff.contactNumberProperty());
        emailText.textProperty().unbindBidirectional(newStaff.emailProperty());
        addressText.textProperty().unbindBidirectional(newStaff.addressProperty());
        usernameText.textProperty().unbindBidirectional(newAccount.usernameProperty());
        accountTypeChoice.valueProperty().unbindBidirectional(newAccount.accountTypeProperty());
        passwordText.setText("");
        confirmPasswordText.setText("");
        newStaff = null;
        newAccount = null;
    }

    private final Model.SaveListener saveListener = new Model.SaveListener() {
        @Override
        public void onSaved() {
            isNewAccount.setValue(false);
            staffInformations.add(newStaff);
            cleanupNewRecords();
        }
    };

    private final Validateable.ValidationListener validationListener = new Validateable.ValidationListener() {
        @Override
        public void onValidated(AbstractList<String> brokenRules) {
            if(brokenRules != null && brokenRules.size() > 0){
                System.out.println("val");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation error");
                alert.setContentText(brokenRules.get(0));
                alert.showAndWait();
            }
        }
    };

    private final Model.ErrorListener errorListener = new Model.ErrorListener() {
        @Override
        public void onError(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("An error occurred");
            alert.setContentText(message);
            alert.showAndWait();
        }
    };

    private final core.service.ErrorListener serviceError = new core.service.ErrorListener() {
        public void onError(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("An error occurred");
            alert.setContentText(message);
            alert.showAndWait();
        }
    };

}
