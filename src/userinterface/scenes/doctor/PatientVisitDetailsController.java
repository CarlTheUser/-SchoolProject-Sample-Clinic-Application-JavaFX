package userinterface.scenes.doctor;

import core.model.*;
import core.util.NumberUtility;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import userinterface.LoginHandle;
import userinterface.navigation.UserNavigation;
import userinterface.scenes.BaseController;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.AbstractList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class PatientVisitDetailsController extends BaseController implements UserNavigation.NavigationListener {

    @FXML Pane container;

    @FXML Label visitDateLabel;

    @FXML Label patientNumberLabel;
    @FXML Label nameLabel;

    @FXML Button patientViewMoreButton;

    @FXML Pane patientDetailsContainer;

    @FXML Label birthdayLabel;
    @FXML Label ageLabel;
    @FXML Label genderLabel;
    @FXML Label contactNumberLabel;
    @FXML Label addressLabel;
    @FXML Label nationalityLabel;
    @FXML Label religionLabel;

    @FXML Label visitNotesLabel;

    @FXML Button beginConsultationButton;
    @FXML Pane consultationBodyContainer;
    @FXML Pane consultationForm;
    @FXML Pane consultationSummary;
    @FXML Button updateVitalsButton;

    @FXML Pane patientVitalsViewPane;

    @FXML Label dateTakenLabel;
    @FXML Label bloodPressureLabel;
    @FXML Label respiratoryRateLabel;
    @FXML Label temperatureLabel;
    @FXML Label heightLabel;
    @FXML Label weightLabel;

    @FXML Pane patientVitalsFormPane;

    @FXML TextField bloodPressureText;
    @FXML TextField respiratoryRateText;
    @FXML TextField temperatureText;
    @FXML TextField heightText;
    @FXML TextField weightText;

    @FXML TextArea patientSymptomsText;
    @FXML TextArea patientDiagnosisText;
    @FXML TextArea doctorNotesText;

    @FXML TextField serviceFeeText;

    @FXML Button saveConsultationButton;

    @FXML Label experiencedSymptomsLabel;
    @FXML Label diagnosisLabel;
    @FXML Label doctorNotesLabel;
    @FXML Label serviceFeeLabel;

    final SimpleBooleanProperty isPatientViewMore = new SimpleBooleanProperty(false);
    final SimpleBooleanProperty isConsulting = new SimpleBooleanProperty(false);
    final SimpleBooleanProperty isVitalsEditing = new SimpleBooleanProperty(false);
    final SimpleBooleanProperty isConsultationFinish = new SimpleBooleanProperty(false);

    public static final String PATIENTVISITRECORD_PARAMETER_NAME = "visit_record";

    VisitRecord visitRecord;
    Patient patient;
    Vitals vitals;
    ConsultationService consultationService;
    StaffInformation doctor;

    @Override public void initialize(URL location, ResourceBundle bundle) {
        super.initialize(location, bundle);
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        getMainView().addNavigationListener(this);
        container.prefWidthProperty().bind(property);
        initializeViewComponents();
        Account a = LoginHandle.getInstance().getCurrentAccount();
        doctor = a.getStaffInformation();
    }



    private void initializeViewComponents() {
        patientViewMoreButton.visibleProperty().bind(isPatientViewMore.not());
        patientDetailsContainer.visibleProperty().bind(isPatientViewMore);
        patientDetailsContainer.managedProperty().bind(patientDetailsContainer.visibleProperty());
        beginConsultationButton.visibleProperty().bind(isConsulting.not());
        consultationBodyContainer.visibleProperty().bind(isConsulting);
        consultationBodyContainer.managedProperty().bind(consultationBodyContainer.visibleProperty());
        consultationForm.visibleProperty().bind(isConsulting.and(isConsultationFinish.not()));
        consultationForm.managedProperty().bind(consultationForm.visibleProperty());
        consultationSummary.visibleProperty().bind(isConsultationFinish);
        consultationSummary.managedProperty().bind(consultationSummary.visibleProperty());
        updateVitalsButton.visibleProperty().bind(isVitalsEditing.not());
        patientVitalsFormPane.visibleProperty().bind(isVitalsEditing);
        patientVitalsFormPane.managedProperty().bind(patientVitalsFormPane.visibleProperty());
        patientVitalsViewPane.visibleProperty().bind(isVitalsEditing.not());
        patientVitalsViewPane.managedProperty().bind(patientVitalsViewPane.visibleProperty());

    }


    @Override protected void onParametersReceived() {
        super.onParametersReceived();
        visitRecord = (VisitRecord)parameters.get(PATIENTVISITRECORD_PARAMETER_NAME);
        bindVisitRecord(visitRecord);
        patient = visitRecord.getPatient();
        bindPatient(patient);
    }

    private void bindPatient(Patient p) {
        patientNumberLabel.textProperty().bind(p.idProperty().asString());
        nameLabel.textProperty().bind(p.fullnameProperty());
        birthdayLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if(p.getBirthdate() == null) return "";
                DateTimeFormatter format = DateTimeFormatter.ofPattern("LLLL dd yyyy");
                return p.getBirthdate().format(format);
            }
        }, p.birthdateProperty()));

        ageLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if(p.getBirthdate() == null) return "";
                String age;
                Period p = Period.between(patient.getBirthdate(), LocalDate.now());
                if(p.getYears() > 0) age = String.format("%d years old %d months", p.getYears(), p.getMonths());
                else if(p.getMonths() > 0) age = String.format("%d months old %d days", p.getMonths(), p.getDays());
                else age = String.format("%d days", p.getDays());
                return age;
            }
        }, p.birthdateProperty()));

        genderLabel.textProperty().bind(p.genderProperty().asString());
        contactNumberLabel.textProperty().bind(p.contactNumberProperty());
        addressLabel.textProperty().bind(p.addressProperty());
        nationalityLabel.textProperty().bind(p.nationalityProperty());
        religionLabel.textProperty().bind(p.religionProperty());

        Vitals v = patient.getVitals();
        if(v == null){
            v = Vitals.NewInstance(patient, vitalsSaveListener, validationListener, errorListener);
            patient.setVitals(v);
        }
        v.setEditListener(editListener);
        this.vitals = v;
        bindVitals(this.vitals);

    }

    private void bindVitals(Vitals vitals) {
        dateTakenLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                LocalDateTime date = vitals.getDateTaken();
                if(date == null) return Vitals.NO_VALUE;
                DateTimeFormatter format = DateTimeFormatter.ofPattern("LLLL dd yyyy hh:mm a");
                return date.format(format);
            }
        }, vitals.dateTakenProperty()));

        bloodPressureLabel.textProperty().bind(vitals.bloodPresureProperty());
        respiratoryRateLabel.textProperty().bind(vitals.respiratoryRateProperty());
        temperatureLabel.textProperty().bind(vitals.temperatureProperty());
        heightLabel.textProperty().bind(vitals.heightProperty());
        weightLabel.textProperty().bind(vitals.weightProperty());

        bloodPressureText.textProperty().bindBidirectional(vitals.bloodPresureProperty());
        respiratoryRateText.textProperty().bindBidirectional(vitals.respiratoryRateProperty());
        temperatureText.textProperty().bindBidirectional(vitals.temperatureProperty());
        heightText.textProperty().bindBidirectional(vitals.heightProperty());
        weightText.textProperty().bindBidirectional(vitals.weightProperty());

    }

    private void bindVisitRecord(VisitRecord visitRecord) {
        visitDateLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("LLLL dd yyyy hh:mm a");
                return visitRecord.getVisitDate().format(format);
            }
        }, visitRecord.visitDateProperty()));
        visitNotesLabel.textProperty().bind(visitRecord.notesProperty());
    }






    @FXML private void showMoreAction(ActionEvent event){
        patient.refresh();
        isPatientViewMore.setValue(true);
    }

    @FXML private void updatePatientVitalsAction(ActionEvent event){
        if(!vitals.isNew()) vitals.beginEdit();
        isVitalsEditing.setValue(true);
    }

    @FXML private void cancelPatientVitalsEditAction(ActionEvent event){
        isVitalsEditing.setValue(false);
        if(!vitals.isNew()) vitals.cancelEdit();
    }

    @FXML private void applyPatientVitalsEditAction(ActionEvent event){
        isVitalsEditing.setValue(false);
        if(vitals.isNew()) vitals.save();
        else vitals.applyEdit();
    }

    @FXML private void beginConsultationAction(ActionEvent event){
        initializeNewConsultation();
        isConsulting.setValue(true);
    }

    @FXML private void saveConsultationAction(ActionEvent event){
        this.consultationService.save();
    }

    private void initializeNewConsultation() {
        this.consultationService = ConsultationService.NewInstance(visitRecord, doctor, consultationSaveListener, validationListener, errorListener);
        bindConsultation(this.consultationService);
    }

    private void bindConsultation(ConsultationService consultationService) {
        patientSymptomsText.textProperty().bindBidirectional(consultationService.symptomsProperty());
        patientDiagnosisText.textProperty().bindBidirectional(consultationService.diagnosisProperty());
        doctorNotesText.textProperty().bindBidirectional(consultationService.doctorNotesProperty());
        serviceFeeText.textProperty().bindBidirectional(consultationService.feeProperty(), new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                NumberFormat format = new DecimalFormat("###0.00");
                return format.format(object);
            }

            @Override
            public Number fromString(String string) {
                if(string.trim().length() > 0){
                    if(NumberUtility.isNumber(string, true)){
                        return Double.parseDouble(string);
                    }
                }
                return 0.0;
            }
        });

        experiencedSymptomsLabel.textProperty().bindBidirectional(consultationService.symptomsProperty());
        diagnosisLabel.textProperty().bind(consultationService.diagnosisProperty());
        doctorNotesLabel.textProperty().bind(consultationService.doctorNotesProperty());
        serviceFeeLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                double fee = consultationService.getFee();
                NumberFormat format = new DecimalFormat("#,##0.00");
                return "Php " + format.format(fee);
            }
        }, consultationService.feeProperty()));
    }









    private final Model.SaveListener vitalsSaveListener = new Model.SaveListener() {
        @Override
        public void onSaved() {

        }
    };

    private final Model.SaveListener consultationSaveListener = new Model.SaveListener() {
        @Override
        public void onSaved() {
            isConsultationFinish.setValue(true);
        }
    };

    private final Model.EditListener editListener = new Model.EditListener() {
        @Override
        public void onEditBegun() {

        }

        @Override
        public void onEditCancelled() {

        }

        @Override
        public void onEditApplied() {

        }
    };

    private final Validateable.ValidationListener validationListener = new Validateable.ValidationListener() {
        @Override
        public void onValidated(AbstractList<String> brokenRules) {
            if(brokenRules.size() > 0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("An error occurred");
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


    @Override
    public void onPreNavigate(PreNavigationArgument argument) {
        if(isConsulting.getValue() || isVitalsEditing.getValue()){

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved changes");
            alert.setContentText("There might be unsaved changes. Continue exit?");
            Optional<ButtonType> b = alert.showAndWait();

            if(b.isPresent() && b.get() == ButtonType.OK){
                getMainView().removeNavigationListener(this);
            } else argument.setCancelNavigate(true);
        } else {
            getMainView().removeNavigationListener(this);
        }
    }
}
