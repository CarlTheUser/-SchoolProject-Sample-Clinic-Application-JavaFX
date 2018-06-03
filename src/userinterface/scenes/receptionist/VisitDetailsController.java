package userinterface.scenes.receptionist;

import core.model.*;
import core.model.readonly.RoomUsage;
import core.service.RoomService;
import core.util.NumberUtility;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import userinterface.URLSource;
import userinterface.customviewelement.ButtonTableCell;
import userinterface.scenes.BaseController;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractList;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class VisitDetailsController extends BaseController {

    @FXML Pane container;

    @FXML Label patientNumberLabel;
    @FXML Label patientNameLabel;
    @FXML Label visitDateLabel;
    @FXML Label visitNotesLabel;

    @FXML Label noAdmissionLabel;
    @FXML Button admitPatientButton;

    @FXML Pane newAdmissionForm;
    @FXML TextArea initialFindingsText;

    @FXML Pane admissionDetails;
    @FXML Label admissionRoomNumberLabel;
    @FXML Label admissionInitialFindingsLabel;

    @FXML Pane finalFindingsForm;
    @FXML Label finalFindingsLabel;
    @FXML Label dischargeDateLabel;

    @FXML Pane dischargeForm;

    @FXML TextArea finalFindingsText;

    @FXML Button dischargePatientButton;

    @FXML TableView<RoomUsage> roomUsageTable;
    @FXML TableColumn<RoomUsage, String> roomNumberColumn;
    @FXML TableColumn<RoomUsage, RoomType> roomTypeColumn;
    @FXML TableColumn<RoomUsage, String> bedUsageColumn;
    @FXML TableColumn<RoomUsage, String> locationDetailsColumn;
    @FXML TableColumn<RoomUsage, Button> actionColumn;


    @FXML Pane newServiceForm;
    @FXML Button newServiceButton;

    @FXML TextField itemNameText;
    @FXML TextField quantityText;
    @FXML TextField unitPriceText;
    @FXML TextField descriptionText;



    @FXML TableView<Service> servicesTable;
    @FXML TableColumn<Service, String> dateColumn;
    @FXML TableColumn<Service, ServiceType> serviceTypeColumn;
    @FXML TableColumn<Service, String> descriptionColumn;
    @FXML TableColumn<Service, Button> serviceActionColumn;



    public static final String VISITRECORD_PARAMETER_NAME = "visit_record";

    VisitRecord visitRecord;

    AdmissionRecord admissionRecord;

    MedicineAndEquipmentService newService;

    DateTimeFormatter format = DateTimeFormatter.ofPattern("LLLL dd yyyy hh:mm a");

    final SimpleObjectProperty<AdmissionStatus> admissionStatus = new SimpleObjectProperty<>();

    final SimpleBooleanProperty isAdmittable = new SimpleBooleanProperty(this, "isAdmittable", false);
    final SimpleBooleanProperty isMakingAdmission = new SimpleBooleanProperty(this, "isMakingAdmission", false);
    final SimpleBooleanProperty hasAdmission = new SimpleBooleanProperty(this, "hasAdmission", false);
    final SimpleBooleanProperty isDischarging = new SimpleBooleanProperty(this, "isDischarging", false);
    final SimpleBooleanProperty isDischarged = new SimpleBooleanProperty(this, "isDischarged", false);

    final SimpleBooleanProperty isNewService = new SimpleBooleanProperty(this, "isNewService", false);

    @Override public void initialize(URL location, ResourceBundle bundle) {
        super.initialize(location, bundle);
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        container.prefWidthProperty().bind(property);
        initializeFormBindings();
    }

    private void initializeFormBindings() {

        noAdmissionLabel.visibleProperty().bind(Bindings.createBooleanBinding(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                AdmissionStatus status = admissionStatus.get();
                return status == AdmissionStatus .NoAdmission  || status == AdmissionStatus.None;
            }
        }, admissionStatus));
        noAdmissionLabel.managedProperty().bind(noAdmissionLabel.visibleProperty());
        admitPatientButton.visibleProperty().bind(Bindings.createBooleanBinding(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return admissionStatus.get() == AdmissionStatus.NoAdmission;
            }
        }, admissionStatus));
        admitPatientButton.managedProperty().bind(admitPatientButton.visibleProperty());
        newAdmissionForm.visibleProperty().bind(Bindings.createBooleanBinding(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return admissionStatus.get() == AdmissionStatus.WritingAdmission;
            }
        }, admissionStatus));
        newAdmissionForm.managedProperty().bind(newAdmissionForm.visibleProperty());
        admissionDetails.visibleProperty().bind(Bindings.createBooleanBinding(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                AdmissionStatus status = admissionStatus.get();
                return status == AdmissionStatus.HasAdmission || status == AdmissionStatus.PastAdmission;
            }
        }, admissionStatus));
        admissionDetails.managedProperty().bind(admissionDetails.visibleProperty());
        finalFindingsForm.visibleProperty().bind(Bindings.createBooleanBinding(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return admissionStatus.get() == AdmissionStatus.PastAdmission;
            }
        }, admissionStatus));
        finalFindingsForm.managedProperty().bind(finalFindingsForm.visibleProperty());

        BooleanBinding dischargingBinding = Bindings.createBooleanBinding(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return admissionStatus.get() == AdmissionStatus.DischargingAdmission;
            }
        }, admissionStatus);

        dischargePatientButton.visibleProperty().bind(Bindings.createBooleanBinding(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return admissionStatus.get() == AdmissionStatus.HasAdmission;
            }
        }, admissionStatus));
        dischargePatientButton.managedProperty().bind(dischargePatientButton.visibleProperty());
        dischargeForm.visibleProperty().bind(dischargingBinding);
        dischargeForm.managedProperty().bind(dischargeForm.visibleProperty());

        newServiceForm.visibleProperty().bind(isNewService);
        newServiceForm.managedProperty().bind(newServiceForm.visibleProperty());
        newServiceButton.visibleProperty().bind(Bindings.createBooleanBinding(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return admissionStatus.get() != AdmissionStatus.PastAdmission;
            }
        }, admissionStatus));
        newServiceButton.managedProperty().bind(newServiceButton.visibleProperty());
//        BooleanBinding notHasAdmission = hasAdmission.not();
//        BooleanBinding notIsMakingAdmission = isMakingAdmission.not();


//        noAdmissionLabel.visibleProperty().bind(notHasAdmission.and(isMakingAdmission.not()));
//        noAdmissionLabel.managedProperty().bind(noAdmissionLabel.visibleProperty());
//        admitPatientButton.visibleProperty().bind(notHasAdmission.and(notIsMakingAdmission).and(isAdmittable));
//        admitPatientButton.managedProperty().bind(admitPatientButton.visibleProperty());
//        newAdmissionForm.visibleProperty().bind(isMakingAdmission);
//        newAdmissionForm.managedProperty().bind(newAdmissionForm.visibleProperty());
//        admissionDetails.visibleProperty().bind(hasAdmission);
//        admissionDetails.managedProperty().bind(admissionDetails.visibleProperty());
//        dischargeForm.visibleProperty().bind(isDischarging);
//        dischargeForm.managedProperty().bind(dischargeForm.visibleProperty());
//        dischargePatientButton.visibleProperty().bind(isDischarging);
//        dischargePatientButton.managedProperty().bind(dischargeForm.visibleProperty());
//        finalFindingsForm.visibleProperty().bind(isDischarged);
//        finalFindingsForm.managedProperty().bind(finalFindingsForm.visibleProperty());
    }

    private void initializeTableBindings(){
        roomNumberColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RoomUsage, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<RoomUsage, String> param) {
                return param.getValue().getRoom().idProperty().asString();
            }
        });

        roomTypeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RoomUsage, RoomType>, ObservableValue<RoomType>>() {
            @Override
            public ObservableValue<RoomType> call(TableColumn.CellDataFeatures<RoomUsage, RoomType> param) {
                return param.getValue().getRoom().typeProperty();
            }
        });

        bedUsageColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RoomUsage, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<RoomUsage, String> param) {
                RoomUsage rs = param.getValue();
                String usage = Integer.toString(rs.getBedsInUse());
                String bedCount = Integer.toString(rs.getRoom().getBedCount());
                ReadOnlyStringWrapper prop = new ReadOnlyStringWrapper();
                prop.setValue(usage + "/" + bedCount);
                return prop.getReadOnlyProperty();
            }
        });

        locationDetailsColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RoomUsage, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<RoomUsage, String> param) {
                return param.getValue().getRoom().locationDetailsProperty();
            }
        });

        actionColumn.setCellFactory(ButtonTableCell.<RoomUsage>forTableColumn("Admit", new Function<RoomUsage, RoomUsage>() {
            @Override
            public RoomUsage apply(RoomUsage roomUsage) {
                admissionRecord.setRoom(roomUsage.getRoom());
                admissionRecord.save();
                return roomUsage;
            }
        }));

        RoomService roomService = new RoomService();
        ObservableList<RoomUsage> roomUsages = FXCollections.observableArrayList(roomService.getAvailableRooms());
        ObservableList<RoomUsage> readOnlyRoomUsages = FXCollections.unmodifiableObservableList(roomUsages);
        roomUsageTable.setItems(readOnlyRoomUsages);
    }

    @Override protected void onParametersReceived() {
        super.onParametersReceived();
        visitRecord = (VisitRecord)parameters.get(VISITRECORD_PARAMETER_NAME);
        bindVisitRecord();
    }

    private void bindVisitRecord() {
        Patient p = visitRecord.getPatient();
        patientNumberLabel.textProperty().bind(p.idProperty().asString());
        patientNameLabel.textProperty().bind(p.fullnameProperty());
        visitDateLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("LLLL dd yyyy hh:mm a");
                return visitRecord.getVisitDate().format(format);
            }
        }, visitRecord.visitDateProperty()));
        visitNotesLabel.textProperty().bind(visitRecord.notesProperty());

        admissionRecord = visitRecord.getAdmission();
        if(admissionRecord != null) {
            bindAdmissionRecord(admissionRecord);
            admissionStatus.set(AdmissionStatus.HasAdmission);
            if(admissionRecord.getDischargeDate() != null) admissionStatus.set(AdmissionStatus.PastAdmission);
        } else if(visitRecord.getVisitDate().toLocalDate().isEqual(LocalDate.now())){
            admissionStatus.set(AdmissionStatus.NoAdmission);
        } else admissionStatus.set(AdmissionStatus.None);

        admissionStatus.get();
    }

    private void bindAdmissionRecord(AdmissionRecord admissionRecord) {
        initialFindingsText.textProperty().bindBidirectional(admissionRecord.initialFindingsProperty());
        if(admissionRecord.getCurrentRoom() != null) admissionRoomNumberLabel.textProperty().bind(admissionRecord.getCurrentRoom().idProperty().asString());
        admissionInitialFindingsLabel.textProperty().bind(admissionRecord.initialFindingsProperty());
        finalFindingsText.textProperty().bindBidirectional(admissionRecord.finalFindingsProperty());
        finalFindingsLabel.textProperty().bind(admissionRecord.finalFindingsProperty());
        dischargeDateLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                LocalDateTime dischargeDate = admissionRecord.getDischargeDate();
                return dischargeDate == null ? "" : dischargeDate.format(format);
            }
        }, admissionRecord.dischargeDateProperty()));
        admissionRecord.setSaveListener(admissionSaveListener);
        admissionRecord.setValidationListener(validationListener);
        admissionRecord.setErrorListener(errorListener);
        admissionRecord.setEditListener(dischargeEditListener);
        initializeAdmissionServicesTableColumnBindings();
    }

    private void initializeAdmissionServicesTableColumnBindings() {

        dateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Service, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Service, String> param) {
                return Bindings.createStringBinding(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        DateTimeFormatter format = DateTimeFormatter.ofPattern("LLLL dd yyyy hh:mm a");
                        return param.getValue().getServieDate().format(format);
                    }
                });
            }
        });
        serviceTypeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Service, ServiceType>, ObservableValue<ServiceType>>() {
            @Override
            public ObservableValue<ServiceType> call(TableColumn.CellDataFeatures<Service, ServiceType> param) {
                return Bindings.createObjectBinding(new Callable<ServiceType>() {
                    @Override
                    public ServiceType call() throws Exception {
                        return param.getValue().getType();
                    }
                });
            }
        });
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Service, String>("description"));
        serviceActionColumn.setCellFactory(ButtonTableCell.<Service>forTableColumn("View", new Function<Service, Service>() {
            @Override
            public Service apply(Service service) {
                if(service.getType() == ServiceType.RoomService){
                    showRoomServiceDetails((core.model.RoomService)service);
                }
                return service;
            }
        }));
        servicesTable.setItems(visitRecord.getServices());
    }

    private void showRoomServiceDetails(core.model.RoomService roomService){
        try {
            FXMLLoader loader = new FXMLLoader(URLSource.getURL("scenes/receptionist/roomservicedetails.fxml"));
            Parent root = loader.load();
            RoomServiceDetailsController controller = loader.getController();
            controller.setRoomService(roomService);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void admitPatientAction(ActionEvent event){
        initializeNewAdmission();
        admissionStatus.set(AdmissionStatus.WritingAdmission);
        initializeTableBindings();
    }

    private void initializeNewAdmission(){
        admissionRecord = AdmissionRecord.NewInstance(visitRecord,null, null, null);
        admissionRecord.setAdmissonDate(LocalDateTime.now());
        bindAdmissionRecord(admissionRecord);
    }

    @FXML private void dischargePatientAction(ActionEvent event){
        admissionRecord.beginEdit();
        admissionStatus.set(AdmissionStatus.DischargingAdmission);
    }

    @FXML private void cancelDischargePatientAction(ActionEvent event){
        admissionStatus.set(AdmissionStatus.HasAdmission);
        admissionRecord.cancelEdit();
    }


    @FXML private void applyDischargePatientAction(ActionEvent event){
        if(admissionRecord.getVisit().hasServicesPaid()) {
            admissionRecord.setDischargeDate(LocalDateTime.now());
            admissionRecord.applyEdit();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot discharge");
            alert.setContentText("There are still unpaid services. Pay any unpaid services to be discharged.");
            alert.showAndWait();
        }
    }

    @FXML private void newServiceAction(ActionEvent event){
        newService = MedicineAndEquipmentService.NewInstance(visitRecord, LocalDateTime.now(), serviceSaveListener, validationListener, errorListener);
        bindMedicineService();
        isNewService.set(true);

    }

    private void bindMedicineService() {
        itemNameText.textProperty().bindBidirectional(newService.itemProperty());
        quantityText.textProperty().bindBidirectional(newService.quantityProperty(), new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return new DecimalFormat("#").format(object);
            }
            @Override
            public Number fromString(String string) {
                if(NumberUtility.isNumber(string)){
                    return Integer.parseInt(string);
                }
                return 0;
            }
        });
        unitPriceText.textProperty().bindBidirectional(newService.unitPriceProperty(), new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return new DecimalFormat("###0.00").format(object);
            }

            @Override
            public Number fromString(String string) {
                if(NumberUtility.isNumber(string, true)){
                    return Double.parseDouble(string);
                }
                return 0;
            }
        });
        descriptionText.textProperty().bindBidirectional(newService.descriptionProperty());
    }

    private void unbindMedicineService(){
        itemNameText.textProperty().unbindBidirectional(newService.itemProperty());
        quantityText.textProperty().unbindBidirectional(newService.quantityProperty());
        unitPriceText.textProperty().unbindBidirectional(newService.unitPriceProperty());
        descriptionText.textProperty().unbindBidirectional(newService.descriptionProperty());
        newService = null;
    }

    @FXML private void applyNewServiceAction(ActionEvent event){
        newService.save();
    }

    @FXML private void cancelNewServiceAction(ActionEvent event){
        isNewService.set(false);
        unbindMedicineService();
    }



    private final Model.SaveListener admissionSaveListener = new Model.SaveListener() {
        @Override
        public void onSaved() {
            if(admissionRecord.getCurrentRoom() != null) admissionRoomNumberLabel.textProperty().bind(admissionRecord.getCurrentRoom().idProperty().asString());
            admissionStatus.set(AdmissionStatus.HasAdmission);
        }
    };

    private final Model.SaveListener serviceSaveListener = new Model.SaveListener() {
        @Override
        public void onSaved() {
            isNewService.set(false);
            visitRecord.addService(newService);
            unbindMedicineService();
        }
    };

    private final Model.EditListener dischargeEditListener = new Model.EditListener() {
        @Override
        public void onEditBegun() {

        }

        @Override
        public void onEditCancelled() {

        }

        @Override
        public void onEditApplied() {
            admissionStatus.set(AdmissionStatus.PastAdmission);
        }
    };

    private final Validateable.ValidationListener validationListener = new Validateable.ValidationListener() {
        @Override
        public void onValidated(AbstractList<String> brokenRules) {
            if(brokenRules.size() > 0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation error");
                alert.setContentText(brokenRules.get(0));
                alert.showAndWait();
            }
        }
    };

    private Model.ErrorListener errorListener = new Model.ErrorListener() {
        @Override
        public void onError(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation error");
            alert.setContentText(message);
            alert.showAndWait();
        }
    };

    private enum AdmissionStatus {
        None(0), NoAdmission(1), WritingAdmission(2), HasAdmission(3), DischargingAdmission(4), PastAdmission(5);
        private final int value;

        AdmissionStatus(int value){
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
