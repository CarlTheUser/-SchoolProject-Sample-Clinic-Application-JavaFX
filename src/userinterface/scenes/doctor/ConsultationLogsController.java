package userinterface.scenes.doctor;

import core.model.ConsultationService;
import core.model.Patient;
import core.model.StaffInformation;
import core.model.readonly.PatientVisit;
import core.service.PatientService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import userinterface.LoginHandle;
import userinterface.URLSource;
import userinterface.customviewelement.ButtonTableCell;
import userinterface.navigation.UserNavigation;
import userinterface.scenes.BaseController;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class ConsultationLogsController extends BaseController {

    @FXML Pane container;

    @FXML TextField searchField;

    @FXML TableView<PatientVisit> patientLogsTable;

    @FXML TableColumn<PatientVisit, Integer> idColumn;
    @FXML TableColumn<PatientVisit, String > patientColumn;
    @FXML TableColumn<PatientVisit, String> dateColumn;
    @FXML TableColumn<PatientVisit, Button> actionColumn;

    ObservableList<PatientVisit> tableSource = FXCollections.observableArrayList();
    AbstractList<PatientVisit> patientVisits = null;
    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        super.initialize(location, bundle);
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        container.prefWidthProperty().bind(property);
        initializeTableColumnBindings();
        StaffInformation staffInformation = LoginHandle.getInstance().getCurrentAccount().getStaffInformation();
        PatientService service = new PatientService();
        tableSource.setAll(patientVisits = service.getConsultingPatients(staffInformation));
        patientLogsTable.setItems(tableSource);
    }

    private void initializeTableColumnBindings() {
        idColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PatientVisit, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<PatientVisit, Integer> param) {
                return param.getValue().getPatient().idProperty().asObject();
            }
        });
        patientColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PatientVisit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PatientVisit, String> param) {
                return param.getValue().getPatient().fullnameProperty();
            }
        });
        dateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PatientVisit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PatientVisit, String> param) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("LLLL dd yyyy hh:mm a");
                return Bindings.createStringBinding(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return param.getValue().getVisitDate().format(format);
                    }
                }, param.getValue().visitDateProperty());
            }
        });
        actionColumn.setCellFactory(ButtonTableCell.<PatientVisit>forTableColumn("View", new Function<PatientVisit, PatientVisit>() {
            @Override
            public PatientVisit apply(PatientVisit patientVisit) {
                Map<String, Object> passedParammeters = new Hashtable<>();
                passedParammeters.put(PatientDetailsController.PATIENT_PARAMETER_NAME, patientVisit.getPatient());
                URL url = URLSource.getURL("scenes/doctor/patientdetails.fxml");
                getMainView().getUserNavigation().navigate(new UserNavigation.NavigationItem(url, false, passedParammeters));
                return patientVisit;
            }
        }));

    }

    @FXML private void textChange(KeyEvent event){
        String query = searchField.getText();

        if(query.length() == 0){
            tableSource.setAll(patientVisits);
            return;
        }

        List<PatientVisit> temp = new ArrayList<>();
        for (PatientVisit patientVisit : patientVisits) {
            if(patientVisit.getPatient().getFullname().toUpperCase().contains(query.toUpperCase())){
                temp.add(patientVisit);
            }
        }
        tableSource.setAll(temp);
    }

}
