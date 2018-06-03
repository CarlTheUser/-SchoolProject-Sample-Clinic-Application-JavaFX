package userinterface.scenes.doctor;

import core.model.Patient;
import core.model.StaffInformation;
import core.model.Vitals;
import core.model.readonly.ConsultationData;
import core.service.ServiceService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import userinterface.LoginHandle;
import userinterface.scenes.BaseController;

import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class PatientDetailsController extends BaseController {

    public static final String PATIENT_PARAMETER_NAME = "patient";

    @FXML Pane container;

    @FXML Label patientNumberLabel;
    @FXML Label nameLabel;
    @FXML Label birthdayLabel;
    @FXML Label ageLabel;
    @FXML Label genderLabel;
    @FXML Label contactNumberLabel;
    @FXML Label addressLabel;
    @FXML Label nationalityLabel;
    @FXML Label religionLabel;

    @FXML Label dateTakenLabel;
    @FXML Label bloodPressureLabel;
    @FXML Label respiratoryRateLabel;
    @FXML Label temperatureLabel;
    @FXML Label heightLabel;
    @FXML Label weightLabel;

    @FXML TableView<ConsultationData> consultationTable;

    @FXML TableColumn<ConsultationData, String> dateColumn;
    @FXML TableColumn<ConsultationData, String> symptomsColumn;
    @FXML TableColumn<ConsultationData, String> diagnosisColumn;
    @FXML TableColumn<ConsultationData, String> doctorNotesColumn;

    Patient patient;
    Vitals vitals;

    private final ServiceService service = new ServiceService();

    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("LLLL dd yyyy hh:mm a");

    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        super.initialize(location, bundle);
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        container.prefWidthProperty().bind(property);
        initializeTableColumnBindings();
    }

    private void initializeTableColumnBindings() {
        dateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ConsultationData, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ConsultationData, String> param) {
                return Bindings.createStringBinding(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return param.getValue().getServiceDate().format(dateTimeFormat);
                    }
                }, param.getValue().serviceDateProperty());
            }
        });

        symptomsColumn.setCellValueFactory(new PropertyValueFactory<ConsultationData, String>("symptoms"));
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<ConsultationData, String>("diagnosis"));
        doctorNotesColumn.setCellValueFactory(new PropertyValueFactory<ConsultationData, String>("doctorNotes"));
    }

    @Override
    protected void onParametersReceived() {
        super.onParametersReceived();
        patient = (Patient)parameters.get(PATIENT_PARAMETER_NAME);
        patient.refresh();
        bindPatient(patient);
    }

    private void bindPatient(Patient patient) {
        patientNumberLabel.textProperty().bind(patient.idProperty().asString());
        nameLabel.textProperty().bind(patient.fullnameProperty());
        birthdayLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return patient.getBirthdate().format(DateTimeFormatter.ofPattern("LLLL dd yyyy"));
            }
        }, patient.birthdateProperty()));
        ageLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String age;
                Period p = Period.between(patient.getBirthdate(), LocalDate.now());
                if(p.getYears() > 0) age = String.format("%d years old %d months", p.getYears(), p.getMonths());
                else if(p.getMonths() > 0) age = String.format("%d months old %d days", p.getMonths(), p.getDays());
                else age = String.format("%d days", p.getDays());
                return age;
            }
        }, patient.birthdateProperty()));

        genderLabel.textProperty().bind(patient.genderProperty().asString());
        contactNumberLabel.textProperty().bind(patient.contactNumberProperty());
        addressLabel.textProperty().bind(patient.addressProperty());
        nationalityLabel.textProperty().bind(patient.nationalityProperty());
        religionLabel.textProperty().bind(patient.religionProperty());

        bindVitals(vitals = patient.getVitals());

        StaffInformation doctor = LoginHandle.getInstance().getCurrentAccount().getStaffInformation();

        ObservableList<ConsultationData> consultationData = FXCollections.observableArrayList(service.getPatientConsultationsFromDoctor(patient, doctor));

        ObservableList<ConsultationData> unmodifiableConsultationData = FXCollections.unmodifiableObservableList(consultationData);

        consultationTable.setItems(unmodifiableConsultationData);

    }

    private void bindVitals(Vitals vitals) {
        if(vitals == null) return;

        dateTakenLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return vitals.getDateTaken().format(dateTimeFormat);
            }
        }, vitals.dateTakenProperty()));
        bloodPressureLabel.textProperty().bind(vitals.bloodPresureProperty());
        respiratoryRateLabel.textProperty().bind(vitals.respiratoryRateProperty());
        temperatureLabel.textProperty().bind(vitals.temperatureProperty());
        heightLabel.textProperty().bind(vitals.heightProperty());
        weightLabel.textProperty().bind(vitals.weightProperty());
    }



}
