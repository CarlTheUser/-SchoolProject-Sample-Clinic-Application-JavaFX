package userinterface.scenes.doctor;

import core.model.Patient;
import core.model.VisitRecord;
import core.service.VisitRecordService;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import userinterface.URLSource;
import userinterface.customviewelement.ButtonTableCell;
import userinterface.navigation.UserNavigation;
import userinterface.scenes.BaseController;
import userinterface.scenes.receptionist.PatientDetailsController;
import userinterface.scenes.receptionist.VisitDetailsController;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

public class CurrentVisitsListController extends BaseController {

    @FXML Pane container;

    @FXML TextField searchField;

    @FXML TableView<VisitRecord> visitRecordsTable;
    @FXML TableColumn<VisitRecord, Integer> visitIdColumn;
    @FXML TableColumn<VisitRecord, String> patientColumn;
    @FXML TableColumn<VisitRecord, String> notesColumn;
    @FXML TableColumn<VisitRecord, LocalDateTime> visitTimeColumn;
    @FXML TableColumn<VisitRecord, Button> actionColumn;


    VisitRecordService service = new VisitRecordService();

    AbstractList<VisitRecord> visitRecords;

    final ObservableList<VisitRecord> tableSource = FXCollections.observableArrayList();

    @Override public void initialize(URL location, ResourceBundle bundle) {
        super.initialize(location, bundle);
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        container.prefWidthProperty().bind(property);
        initializeViewComponents();

        tableSource.setAll(visitRecords = service.getRecentVisits());
        visitRecordsTable.setItems(tableSource);
    }

    private void initializeViewComponents() {
        initializeTableBindings();
    }

    private void initializeTableBindings() {
        visitIdColumn.setCellValueFactory(new PropertyValueFactory<VisitRecord, Integer>("id"));
        patientColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VisitRecord, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<VisitRecord, String> param) {
                return param.getValue().getPatient().fullnameProperty();
            }
        });
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        visitTimeColumn.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
        visitTimeColumn.setCellFactory(col -> new TableCell<VisitRecord, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else{
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
                    setText(String.format(item.format(formatter)));
                }
            }
        });

        actionColumn.setCellFactory(ButtonTableCell.<VisitRecord>forTableColumn("View", new Function<VisitRecord, VisitRecord>() {
            @Override
            public VisitRecord apply(VisitRecord visitRecord) {
                Map<String, Object> passedParammeters = new Hashtable<>();
                passedParammeters.put(VisitDetailsController.VISITRECORD_PARAMETER_NAME, visitRecord);
                URL url = URLSource.getURL("scenes/doctor/patientvisitdetails.fxml");
                getMainView().getUserNavigation().navigate(new UserNavigation.NavigationItem(url, false, passedParammeters));
                return visitRecord;
            }
        }));
    }

    @FXML private void textChange(KeyEvent event){
        if(visitRecords.size() == 0) return;
        String query = searchField.getText();

        if(query.length() == 0){
            tableSource.setAll(visitRecords);
            return;
        }

        List<VisitRecord> temp = new ArrayList<>();
        for (VisitRecord visit: visitRecords) {
            if(visit.getPatient().getFullname().toUpperCase().contains(query.toUpperCase())){
                temp.add(visit);
            }
        }
        tableSource.setAll(temp);
    }
}
