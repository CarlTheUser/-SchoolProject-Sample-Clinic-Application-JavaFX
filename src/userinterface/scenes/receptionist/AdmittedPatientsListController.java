package userinterface.scenes.receptionist;

import core.model.AdmissionRecord;
import core.model.Patient;
import core.service.AdmissionService;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import userinterface.scenes.BaseController;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdmittedPatientsListController extends BaseController implements Initializable {

    @FXML Pane defaultContainer;
    @FXML TextField txFindPatient;

    @FXML TextField searchField;

    @FXML TableView<AdmissionRecord> admissionsTable;

    @FXML TableColumn<AdmissionRecord, Integer> patientIdColumn;
    @FXML TableColumn<AdmissionRecord, String> patientNameColumn;
    @FXML TableColumn<AdmissionRecord, String> initialFindingsColumn;
    @FXML TableColumn<AdmissionRecord, String> dateAdmittedColumn;
    @FXML TableColumn<AdmissionRecord, Integer> roomNumberColumn;
    @FXML TableColumn<AdmissionRecord, String> locationDetailsColumn;

    final AdmissionService admissionService = new AdmissionService();

    ObservableList<AdmissionRecord> tableSource;

    AbstractList<AdmissionRecord> admissons;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeViewComponents();
    }

    private void initializeViewComponents() {
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        defaultContainer.prefWidthProperty().bind(property);
        initializeTableColumnBindings();
        admissons = admissionService.getCurrentAdmissions();
        admissionsTable.setItems(tableSource = FXCollections.observableArrayList(admissons));

    }

    private void initializeTableColumnBindings() {
        patientIdColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AdmissionRecord, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<AdmissionRecord, Integer> param) {
                return param.getValue().getVisit().getPatient().idProperty().asObject();
            }
        });
        patientNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AdmissionRecord, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<AdmissionRecord, String> param) {
                return param.getValue().getVisit().getPatient().fullnameProperty();
            }
        });
        initialFindingsColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AdmissionRecord, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<AdmissionRecord, String> param) {
                return param.getValue().initialFindingsProperty();
            }
        });
        dateAdmittedColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AdmissionRecord, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<AdmissionRecord, String> param) {
                SimpleStringProperty property = new SimpleStringProperty();
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("LLLL MM yyyy hh:mm a");
                property.setValue(param.getValue().getAdmissonDate().format(dateFormat));
                return property;
            }
        });
        roomNumberColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AdmissionRecord, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<AdmissionRecord, Integer> param) {
                return param.getValue().getCurrentRoom().idProperty().asObject();
            }
        });
        locationDetailsColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AdmissionRecord, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<AdmissionRecord, String> param) {
                return param.getValue().getCurrentRoom().locationDetailsProperty();
            }
        });
    }

    @FXML private void textChange(KeyEvent event){
        String query = searchField.getText();

        if(query.length() == 0){
            tableSource.setAll(admissons);
            return;
        }

        List<AdmissionRecord> temp = new ArrayList<>();
        for (AdmissionRecord admission : admissons) {
            if(admission.getVisit().getPatient().getFullname().toUpperCase().contains(query.toUpperCase())){
                temp.add(admission);
            }
        }
        tableSource.setAll(temp);
    }
}
