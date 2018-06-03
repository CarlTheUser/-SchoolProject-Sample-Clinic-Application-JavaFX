package userinterface.scenes.receptionist;

import core.model.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import userinterface.URLSource;
import userinterface.customviewelement.ButtonTableCell;
import userinterface.navigation.UserNavigation;
import userinterface.scenes.BaseController;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class PatientDetailsController extends BaseController implements UserNavigation.NavigationListener {

    public static final String PATIENT_PARAMETER_NAME = "patient";

    @FXML Pane container;

    @FXML Label labelPatientNumber;
    @FXML Label labelName;
    @FXML Label labelBirthdate;
    @FXML Label labelAge;
    @FXML Label labelGender;
    @FXML Label labelContactNumber;
    @FXML Label labelAddress;
    @FXML Label labelNationality;
    @FXML Label labelReligion;

    @FXML TextField txFirstname;
    @FXML TextField txMiddlename;
    @FXML TextField txLastname;
    @FXML DatePicker dpBirthdate;
    @FXML ChoiceBox<Gender> cbGender;
    @FXML TextField txContactNumber;
    @FXML TextField txAddress;
    @FXML TextField txReligion;
    @FXML TextField txNationality;

    @FXML Pane patientDetailsPane;
    @FXML Pane patientEditPane;

    @FXML TableView<Disease> tbConditions;

    @FXML TableColumn<Disease, String> conditionColumn;
    @FXML TableColumn<Disease, String> statusColumn;
    @FXML TableColumn<Disease, String> descriptionColumn;


    @FXML TableView<ContactPerson> tbContacts;

    @FXML TableColumn<ContactPerson, String> nameColumn;
    @FXML TableColumn<ContactPerson, String> contactNumberColumn;
    @FXML TableColumn<ContactPerson, String> relationshipColumn;
    @FXML TableColumn<ContactPerson, String> addressColumn;


    @FXML TableView<VisitRecord> tbVisits;

    @FXML TableColumn<VisitRecord, Integer> idColumn;
    @FXML TableColumn<VisitRecord, String > visitDateColumn;
    @FXML TableColumn<VisitRecord, String> notesColumn;
    @FXML TableColumn<VisitRecord, Button> viewColumn;

    Stage stage;

    @FXML Label labelNewVisitDate;

    @FXML TextArea taNewVisitNotes;

    LocalDateTime visitTime;

    @FXML TextField txContactPersonName;

    @FXML TextField txContactPersonNumber;
    @FXML TextField txContactPersonAddress;
    @FXML ComboBox<String> cbContactPersonRelation;

    @FXML ComboBox<String> cbConditionName;
    @FXML TextField txConditionStatus;
    @FXML TextArea taConditionDescription;

    private Disease newCondition;

    private ContactPerson newContactPerson;

    private VisitRecord newVisitRecord;

    Model selectedModel;

    Patient patient;

    private ObservableList<String> commonConditions;

    private ObservableList<String> commonRelations;

    @Override public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        container.prefWidthProperty().bind(property);
        initializeViewComponents();
        getMainView().addNavigationListener(this);
        String[] conditions = new String[] {"Cancer", "Tuberculosis", "Hydrocephalus", "Elephantiasis", "Gangrene"};
        commonConditions = FXCollections.observableArrayList(conditions);
        String[] relations = new String[] {"Mother", "Father", "Son", "Daughter", "Sibling", "Grand Father", "Grand Mother", "Partner", "Neighbour", "Spouse"};
        commonRelations = FXCollections.observableArrayList(relations);
    }

    private void initializeViewComponents() {
        cbGender.setItems(FXCollections.observableArrayList(Gender.values()));
        initializeTableFields();
    }

    private void initializeTableBindings() {
        ObservableList<ContactPerson> contacts = patient.getContactPeople();
        ObservableList<Disease> conditions = patient.getConditions();
        ObservableList<VisitRecord> visits = patient.getVisitRecords();

        tbContacts.setItems(contacts);
        tbConditions.setItems(conditions);
        tbVisits.setItems(visits);
    }

    private void initializePatientBindings(){
        labelPatientNumber.textProperty().bind(patient.idProperty().asString());
        labelName.textProperty().bind(patient.fullnameProperty());
        labelBirthdate.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("LLLL dd yyyy");
                return patient.birthdateProperty().get().format(dateFormat);
            }
        }, patient.birthdateProperty()));
        labelAge.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
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
        labelGender.textProperty().bind(patient.genderProperty().asString());
        labelContactNumber.textProperty().bind(patient.contactNumberProperty());
        labelAddress.textProperty().bind(patient.addressProperty());
        labelNationality.textProperty().bind(patient.nationalityProperty());
        labelReligion.textProperty().bind(patient.religionProperty());

        txFirstname.textProperty().bindBidirectional(patient.firstnameProperty());
        txMiddlename.textProperty().bindBidirectional(patient.middlenameProperty());
        txLastname.textProperty().bindBidirectional(patient.lastnameProperty());
        dpBirthdate.valueProperty().bindBidirectional(patient.birthdateProperty());
        cbGender.valueProperty().bindBidirectional(patient.genderProperty());
        txContactNumber.textProperty().bindBidirectional(patient.contactNumberProperty());
        txAddress.textProperty().bindBidirectional(patient.addressProperty());
        txReligion.textProperty().bindBidirectional(patient.religionProperty());
        txNationality.textProperty().bindBidirectional(patient.nationalityProperty());

        patientDetailsPane.visibleProperty().bind(patient.isEditingProperty().not());
        patientEditPane.visibleProperty().bind(patient.isEditingProperty());
        patientDetailsPane.managedProperty().bind(patientDetailsPane.visibleProperty());
        patientEditPane.managedProperty().bind(patientEditPane.visibleProperty());

        patient.setValidationListener(validationListener);
        patient.setErrorListener(errorListener);

        initializeTableBindings();
    }

    private void initializeTableFields() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<ContactPerson, String>("name"));
        contactNumberColumn.setCellValueFactory(new PropertyValueFactory<ContactPerson, String>("contactNumber"));
        relationshipColumn.setCellValueFactory(new PropertyValueFactory<ContactPerson, String>("relation"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<ContactPerson, String>("address"));

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        contactNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        relationshipColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        addressColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        conditionColumn.setCellValueFactory(new PropertyValueFactory<Disease, String>("name"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<Disease, String>("status"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Disease, String>("description"));

        conditionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        statusColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        idColumn.setCellValueFactory(new PropertyValueFactory<VisitRecord, Integer>("id"));
        visitDateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VisitRecord, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<VisitRecord, String> param) {
                SimpleStringProperty property = new SimpleStringProperty();
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("LLLL MM yyyy hh:mm a");
                property.setValue(param.getValue().getVisitDate().format(dateFormat));
                return property;
            }
        });
        notesColumn.setCellValueFactory(new PropertyValueFactory<VisitRecord, String>("notes"));
        viewColumn.setCellFactory(ButtonTableCell.<VisitRecord>forTableColumn("View", new Function<VisitRecord, VisitRecord>() {
            @Override
            public VisitRecord apply(VisitRecord visitRecord) {
                Map<String, Object> passedParammeters = new Hashtable<>();
                passedParammeters.put(VisitDetailsController.VISITRECORD_PARAMETER_NAME, visitRecord);
                URL url = URLSource.getURL("scenes/receptionist/visitdetails.fxml");
                getMainView().getUserNavigation().navigate(new UserNavigation.NavigationItem(url, false, passedParammeters));
                return visitRecord;
            }
        }));

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        contactNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        relationshipColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        addressColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        conditionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        statusColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        tbConditions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Disease>() {
            @Override
            public void changed(ObservableValue<? extends Disease> observable, Disease oldValue, Disease newValue) {
                if(newValue != null) selectedModel = newValue;
            }
        });

//        tbConditions.getSelectionModel().selectedItemProperty().addListener(tableSelectionListener);
//        tbContacts.getSelectionModel().selectedItemProperty().addListener(tableSelectionListener);
    }

//    private final ChangeListener<Model> tableSelectionListener = new ChangeListener<Model>() {
//        @Override
//        public void changed(ObservableValue<? extends Model> observable, Model oldValue, Model newValue) {
//            if(newValue != null) attachEventListeners(selectedModel = newValue);
//        }
//    };


    @Override protected void onParametersReceived() {
        System.out.println("PatientDetailsController handling onParametersReceived line 159");
        super.onParametersReceived();
        System.out.println("PatientDetailsController handling onParametersReceived line 161");
        patient = (Patient)parameters.get(PATIENT_PARAMETER_NAME);
        initializePatientBindings();
    }




    @FXML void contactPersonColumnEditCancelEvent(TableColumn.CellEditEvent event){
        ContactPerson selected = tbContacts.getSelectionModel().getSelectedItem();
        selected.cancelEdit();
    }

    @FXML void contactPersonColumnEditStartEvent(TableColumn.CellEditEvent event){
        ContactPerson selected = tbContacts.getSelectionModel().getSelectedItem();
        attachEventListeners(selectedModel = selected);
        selected.beginEdit();
    }

    @FXML void nameColumnEditCommitEvent(TableColumn.CellEditEvent event){
        ContactPerson selected = tbContacts.getSelectionModel().getSelectedItem();
        selected.setName(event.getNewValue().toString());
        selected.applyEdit();
    }

    @FXML void contactNumberColumnEditCommitEvent(TableColumn.CellEditEvent event){
        ContactPerson selected = tbContacts.getSelectionModel().getSelectedItem();
        selected.setContactNumber(event.getNewValue().toString());
        selected.applyEdit();
    }

    @FXML void relationshipColumnEditCommitEvent(TableColumn.CellEditEvent event){
        ContactPerson selected = tbContacts.getSelectionModel().getSelectedItem();
        selected.setRelation(event.getNewValue().toString());
        selected.applyEdit();
    }

    @FXML void addressColumnEditCommitEvent(TableColumn.CellEditEvent event){
        ContactPerson selected = tbContacts.getSelectionModel().getSelectedItem();
        selected.setAddress(event.getNewValue().toString());
        selected.applyEdit();
    }



    @FXML void conditionColumnEditStartEvent(TableColumn.CellEditEvent event){
        Disease selected = tbConditions.getSelectionModel().getSelectedItem();
        attachEventListeners(selectedModel = selected);
        selected.beginEdit();
    }

    @FXML void conditionColumnEditCancelEvent(TableColumn.CellEditEvent event){
        Disease selected = tbConditions.getSelectionModel().getSelectedItem();
        selected.cancelEdit();
    }

    @FXML void conditionColumnEditCommitEvent(TableColumn.CellEditEvent event){
        Disease selected = tbConditions.getSelectionModel().getSelectedItem();
        selected.setName(event.getNewValue().toString());
        selected.applyEdit();
    }

    @FXML void statusColumnEditCommitEvent(TableColumn.CellEditEvent event){
        Disease selected = tbConditions.getSelectionModel().getSelectedItem();
        selected.setStatus(event.getNewValue().toString());
        selected.applyEdit();
    }

    @FXML void descriptionColumnEditCommitEvent(TableColumn.CellEditEvent event){
        Disease selected = tbConditions.getSelectionModel().getSelectedItem();
        selected.setDescription(event.getNewValue().toString());
        selected.applyEdit();
    }







    private void attachEventListeners(Model model){
        model.setEditListener(editListener);
        model.setValidationListener(validationListener);
        model.setErrorListener(errorListener);
    }

    private void detachEventListeners(Model model){
        model.setEditListener(null);
        model.setValidationListener(null);
        model.setErrorListener(null);
    }

    @FXML private void editPersonAction(ActionEvent event) {
        patient.beginEdit();
    }

    @FXML private void cancelEditPersonAncton(ActionEvent event){
        patient.cancelEdit();
    }

    @FXML private void applyEditPersonAction(ActionEvent event){
        patient.applyEdit();
    }

    @FXML private void newConditionAction(ActionEvent event){
        stage = (stage == null) ? new Stage() : stage;
        try {
            stage.setTitle("New Patient Condition");
            FXMLLoader loader = new FXMLLoader(URLSource.getURL("scenes/receptionist/newcondition.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
        initializeNewCondition();
    }

    @FXML private void conditionAddAction(ActionEvent event){
        newCondition.save();
    }

    @FXML private void cancelNewConditionAction(ActionEvent event){
        stage.hide();
        cleanupNewConditionBindings();
        newCondition = null;
    }

    @FXML private void newContactPersonAction(ActionEvent event){
        stage = (stage == null) ? new Stage() : stage;
        try {
            stage.setTitle("New Contact Person");
            FXMLLoader loader = new FXMLLoader(URLSource.getURL("scenes/receptionist/newcontactperson.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
        initializeNewContactPerson();
    }

    @FXML private void contactPersonAddAction(ActionEvent event){
        newContactPerson.save();
    }

    @FXML private void cancelNewContactPersonAction(ActionEvent event){
        stage.hide();
        cleanupNewContactPersonBindings();
        newContactPerson = null;
    }

    @FXML private void newVisitAction(ActionEvent event){
        stage = (stage == null) ? new Stage() : stage;
        try {
            stage.setTitle("New Visit Record");
            FXMLLoader loader = new FXMLLoader(URLSource.getURL("scenes/receptionist/newvisit.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("hh:mm a LLLL dd yyyy");
        labelNewVisitDate.setText((visitTime = LocalDateTime.now()).format(dateTimeFormat));
        initializeNewVisitRecord();
    }

    @FXML private void saveNewVisitAction(ActionEvent event){
        newVisitRecord.save();
    }

    @FXML private void cancelNewVisitAction(ActionEvent event){
        stage.hide();
        cleanupNewVisitBindings();
        newVisitRecord = null;
    }

    @Override
    public void onPreNavigate(PreNavigationArgument argument) {
        if(patient.isEditing()){

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved changes");
            alert.setContentText("There might be unsaved changes. Cancel edit?");
            Optional<ButtonType> b = alert.showAndWait();

            if(b.isPresent() && b.get() == ButtonType.OK){
                getMainView().removeNavigationListener(this);
            } else argument.setCancelNavigate(true);
        } else {
            getMainView().removeNavigationListener(this);
        }
    }


    private void initializeNewCondition(){
        newCondition = Disease.NewInstance(newConditionSaveListener, validationListener, errorListener);
        cbConditionName.setItems(commonConditions);
        cbConditionName.valueProperty().bindBidirectional(newCondition.nameProperty());
        txConditionStatus.textProperty().bindBidirectional(newCondition.statusProperty());
        taConditionDescription.textProperty().bindBidirectional(newCondition.descriptionProperty());
        newCondition.setPatient(patient);
    }

    private void cleanupNewConditionBindings() {
        if(newCondition != null){
            newCondition.setSaveListener(null);
            newCondition.setValidationListener(null);
            newCondition.setErrorListener(null);
        }
        if(cbConditionName.valueProperty().isBound()) cbConditionName.valueProperty().unbind();
        if(txConditionStatus.textProperty().isBound()) txConditionStatus.textProperty().unbind();
        if(taConditionDescription.textProperty().isBound()) taConditionDescription.textProperty().unbind();
    }

    private void initializeNewContactPerson(){
        newContactPerson = ContactPerson.NewInstance(newContactPersonSaveListener, validationListener, errorListener);
        txContactPersonName.textProperty().bindBidirectional(newContactPerson.nameProperty());
        txContactPersonNumber.textProperty().bindBidirectional(newContactPerson.contactNumberProperty());
        txContactPersonAddress.textProperty().bindBidirectional(newContactPerson.addressProperty());
        cbContactPersonRelation.valueProperty().bindBidirectional(newContactPerson.relationProperty());
        cbContactPersonRelation.setItems(commonRelations);
        newContactPerson.setPatient(patient);
    }

    private void cleanupNewContactPersonBindings() {
        if(newContactPerson != null){
            newContactPerson.setSaveListener(null);
            newContactPerson.setValidationListener(null);
            newContactPerson.setErrorListener(null);
        }
        if(txContactPersonName.textProperty().isBound()) txContactPersonName.textProperty().unbind();
        if(txContactPersonNumber.textProperty().isBound()) txContactPersonNumber.textProperty().unbind();
        if(txContactPersonAddress.textProperty().isBound()) txContactPersonAddress.textProperty().unbind();
        if(cbContactPersonRelation.valueProperty().isBound()) cbContactPersonRelation.valueProperty().unbind();
    }

    private void initializeNewVisitRecord(){
        newVisitRecord = VisitRecord.NewInstance(newVisitSaveListener, validationListener, errorListener);
        taNewVisitNotes.textProperty().bindBidirectional(newVisitRecord.notesProperty());
        newVisitRecord.setVisitDate(visitTime);
        newVisitRecord.setPatient(patient);
    }

    private void cleanupNewVisitBindings() {
        if(newVisitRecord != null){
            newVisitRecord.setValidationListener(null);
            newVisitRecord.setSaveListener(null);
            newVisitRecord.setErrorListener(null);
        }
        if(taNewVisitNotes.textProperty().isBound()) taNewVisitNotes.textProperty().unbind();
    }


    private final Model.SaveListener newConditionSaveListener = new Model.SaveListener() {
        @Override
        public void onSaved() {
            stage.hide();
            patient.addCondition(newCondition);
            cleanupNewConditionBindings();
            newCondition = null;
        }
    };

    private final Model.SaveListener newContactPersonSaveListener = new Model.SaveListener() {
        @Override
        public void onSaved() {
            stage.hide();
            patient.addContactPerson(newContactPerson);
            cleanupNewContactPersonBindings();
            newContactPerson = null;
        }
    };

    private final Model.SaveListener newVisitSaveListener = new Model.SaveListener() {
        @Override
        public void onSaved() {
            stage.hide();
            patient.addVisitRecord(newVisitRecord);
            cleanupNewVisitBindings();
            newVisitRecord = null;
        }
    };

    private final Model.EditListener editListener = new Model.EditListener() {
        @Override
        public void onEditBegun() {

        }

        @Override
        public void onEditCancelled() {
            detachEventListeners(selectedModel);
            selectedModel = null;
        }

        @Override
        public void onEditApplied() {
            detachEventListeners(selectedModel);
            selectedModel = null;
        }
    };

    private final Validateable.ValidationListener validationListener = new Validateable.ValidationListener() {
        @Override
        public void onValidated(AbstractList<String> brokenRules) {
            if(brokenRules != null && brokenRules.size() > 0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation error");
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


}
