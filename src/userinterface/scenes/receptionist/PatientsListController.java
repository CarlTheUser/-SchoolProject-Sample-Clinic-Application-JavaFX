package userinterface.scenes.receptionist;

import core.model.Gender;
import core.model.Patient;
import core.service.PatientService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import userinterface.URLSource;
import userinterface.customviewelement.ButtonTableCell;
import userinterface.navigation.UserNavigation;
import userinterface.scenes.BaseController;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class PatientsListController extends BaseController implements Initializable {

    @FXML Pane defaultContainer;

    @FXML TableView<Patient> tbPatients;

    @FXML TableColumn<Patient, Integer> idColumn;
    @FXML TableColumn<Patient, String> nameColumn;
    @FXML TableColumn<Patient, Integer> ageColumn;
    @FXML TableColumn<Patient, Gender> genderColumn;
    @FXML TableColumn<Patient, String> contactNumberColumn;
    @FXML TableColumn<Patient, Button> viewColumn;

    @FXML TextField searchField;

    PatientService patientService = new PatientService();

    ObservableList<Patient> tableSource;

    AbstractList<Patient> patients;

    @Override public void initialize(URL location, ResourceBundle resources) {
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        defaultContainer.prefWidthProperty().bind(property);
        initializeViewComponents();
    }

    private void initializeViewComponents() {
        initializeTableBindings();
    }

    private void initializeTableBindings() {

        idColumn.setCellValueFactory(new PropertyValueFactory<Patient, Integer>("id"));

        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Patient, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Patient, String > param) {
                SimpleStringProperty prop = new SimpleStringProperty(param.getValue().getFullname());
                prop.bind(Bindings.createStringBinding(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return param.getValue().getFullname();
                    }
                }));
                return prop;
            }
        });
        ageColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Patient, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Patient, Integer> param) {
                SimpleIntegerProperty prop = new SimpleIntegerProperty(param.getValue().getAge());
                prop.bind(Bindings.createIntegerBinding(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return param.getValue().getAge();
                    }
                }));
                return prop.asObject();
            }
        });
        genderColumn.setCellValueFactory(new PropertyValueFactory<Patient, Gender>("gender"));
        contactNumberColumn.setCellValueFactory(new PropertyValueFactory<Patient, String>("contactNumber"));

        viewColumn.setCellFactory(ButtonTableCell.<Patient>forTableColumn("View", new Function<Patient, Patient>() {
            @Override
            public Patient apply(Patient patient) {
                Map<String, Object> passedParammeters = new Hashtable<>();
                passedParammeters.put(PatientDetailsController.PATIENT_PARAMETER_NAME, patient);
                URL url = URLSource.getURL("scenes/receptionist/patientdetails.fxml");
                getMainView().getUserNavigation().navigate(new UserNavigation.NavigationItem(url, false, passedParammeters));
                return patient;
            }
        }));

        tbPatients.setItems(tableSource = FXCollections.observableArrayList(patients = patientService.getAllPatients()));
    }

    @FXML private void textChange(KeyEvent event){
        String query = searchField.getText();

        if(query.length() == 0){
            tableSource.setAll(patients);
            return;
        }

        List<Patient> temp = new ArrayList<>();
        for (Patient patient : patients) {
            if(patient.getFullname().toUpperCase().contains(query.toUpperCase())){
                temp.add(patient);
            }
        }
        tableSource.setAll(temp);
    }

}
