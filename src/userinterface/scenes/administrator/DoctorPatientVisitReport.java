package userinterface.scenes.administrator;

import core.model.readonly.DoctorPatientVisit;
import core.service.StaffService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import userinterface.scenes.BaseController;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class DoctorPatientVisitReport extends BaseController {

    @FXML Pane container;

    @FXML TableView<DoctorPatientVisit> table;

    @FXML TableColumn<DoctorPatientVisit, String> doctorColumn;
    @FXML TableColumn<DoctorPatientVisit, String> patientColumn;
    @FXML TableColumn<DoctorPatientVisit, String> lastVisitColumn;

    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("LLLL dd yyyy hh:mm a");

    @Override public void initialize(URL location, ResourceBundle bundle) {
        super.initialize(location, bundle);
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        container.prefWidthProperty().bind(property);
        initializeTableColumnBindings();
        StaffService service = new StaffService();
        table.setItems(FXCollections.observableArrayList(service.getAllDoctorPatient()));
    }


    private void initializeTableColumnBindings() {
        doctorColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DoctorPatientVisit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DoctorPatientVisit, String> param) {
                return param.getValue().getDoctor().fullnameProperty();
            }
        });

        patientColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DoctorPatientVisit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DoctorPatientVisit, String> param) {
                return param.getValue().getPatient().fullnameProperty();
            }
        });

        lastVisitColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DoctorPatientVisit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DoctorPatientVisit, String> param) {
                return Bindings.createStringBinding(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return param.getValue().getLastVisit().format(format);
                    }
                });
            }
        });
    }

}
