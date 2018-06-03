package userinterface.scenes.cashier;

import core.model.Patient;
import core.service.PatientService;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import userinterface.URLSource;
import userinterface.customviewelement.ButtonTableCell;
import userinterface.navigation.UserNavigation;
import userinterface.scenes.BaseController;


import java.net.URL;
import java.util.*;
import java.util.function.Function;

public class PatientListController extends BaseController {

    @FXML
    Pane container;

    @FXML TableView<Patient> patientsTable;

    @FXML TableColumn<Patient, Integer> idColumn;
    @FXML TableColumn<Patient, String> patientColumn;
    @FXML TableColumn<Patient, Button> actionColumn;

    @FXML TextField searchField;

    AbstractList<Patient> source;

    final ObservableList<Patient> tableSource = FXCollections.observableArrayList();

    PatientService service = new PatientService();

    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        super.initialize(location, bundle);
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        container.prefWidthProperty().bind(property);
        initializeTableColumnBindings();
        tableSource.addAll(source = service.getAllPatients());
        patientsTable.setItems(tableSource);
    }

    private void initializeTableColumnBindings() {
        idColumn.setCellValueFactory(new PropertyValueFactory<Patient, Integer>("id"));
        patientColumn.setCellValueFactory(new PropertyValueFactory<Patient, String>("fullname"));
        actionColumn.setCellFactory(ButtonTableCell.<Patient>forTableColumn("View Balance", new Function<Patient, Patient>() {
            @Override
            public Patient apply(Patient patient) {
                Map<String, Object> passedParammeters = new Hashtable<>();
                passedParammeters.put(PatientBalanceController.PATIENT_PARAMETER_NAME, patient);
                URL url = URLSource.getURL("scenes/cashier/patientbalance.fxml");
                getMainView().getUserNavigation().navigate(new UserNavigation.NavigationItem(url, false, passedParammeters));
                return patient;
            }
        }));

    }

    @FXML private void textChange(KeyEvent event){
        String query = searchField.getText();

        if(query.length() == 0){
            tableSource.setAll(source);
            return;
        }

        List<Patient> temp = new ArrayList<>();
        for (Patient patient : source) {
            if(patient.getFullname().toUpperCase().contains(query.toUpperCase())){
                temp.add(patient);
            }
        }
        tableSource.setAll(temp);
    }


}
