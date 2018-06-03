package userinterface.scenes.receptionist;

import core.model.*;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import userinterface.URLSource;
import userinterface.navigation.UserNavigation;
import userinterface.scenes.BaseController;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class NewPatientController extends BaseController implements Initializable, UserNavigation.NavigationListener, Model.ErrorListener, Model.SaveListener, Validateable.ValidationListener {

    @FXML Pane container;

    @FXML TextField txFirstname;
    @FXML TextField txMiddlename;
    @FXML TextField txLastname;
    @FXML DatePicker dpBirthdate;
    @FXML ChoiceBox<Gender> cbGender;
    @FXML TextField txContactNumber;
    @FXML TextField txAddress;
    @FXML TextField txReligion;
    @FXML TextField txNationality;

    private final Patient patient = Patient.NewInstance(this, this, this);

    private Disease newCondition;

    private ContactPerson newContactPerson;

    @Override public void initialize(URL location, ResourceBundle resources) {
        initializeViewComponents();
        getMainView().addNavigationListener(this::onPreNavigate);
    }

    private void initializeViewComponents() {
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        container.prefWidthProperty().bind(property);
        initializePropertyBindings();
    }

    private void initializePropertyBindings() {
        cbGender.setItems(FXCollections.observableArrayList(Gender.values()));
        txFirstname.textProperty().bindBidirectional(patient.firstnameProperty());
        txMiddlename.textProperty().bindBidirectional(patient.middlenameProperty());
        txLastname.textProperty().bindBidirectional(patient.lastnameProperty());
        dpBirthdate.valueProperty().bindBidirectional(patient.birthdateProperty());
        cbGender.valueProperty().bindBidirectional(patient.genderProperty());
        txContactNumber.textProperty().bindBidirectional(patient.contactNumberProperty());
        txAddress.textProperty().bindBidirectional(patient.addressProperty());
        txReligion.textProperty().bindBidirectional(patient.religionProperty());
        txNationality.textProperty().bindBidirectional(patient.nationalityProperty());
    }


    @Override public void onPreNavigate(PreNavigationArgument argument) {
//        if(patient.isDirty()){
//            //TODO show confirmation messagebox
//            //argument.setCancelNavigate(true);
////            getMainView().removeNavigationListener(this::onPreNavigate);
//        }
        getMainView().removeNavigationListener(this::onPreNavigate);
    }

    @FXML private void saveAction(ActionEvent event) {
        patient.save();
    }

    @Override
    public void onError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void onSaved() {
        Map<String, Object> passedParammeters = new Hashtable<>();
        passedParammeters.put(PatientDetailsController.PATIENT_PARAMETER_NAME, patient);
        URL url = URLSource.getURL("scenes/receptionist/patientdetails.fxml");
        getMainView().getUserNavigation().navigate(new UserNavigation.NavigationItem(url, false, passedParammeters));
    }

    @Override
    public void onValidated(AbstractList<String> brokenRules) {
        if(brokenRules.size() > 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation error");
            alert.setContentText(brokenRules.get(0));
            alert.showAndWait();
        }
    }
}
