package core.model;

import core.service.AdmissionService;
import core.service.PatientService;
import core.service.ServiceService;
import core.service.VisitRecordService;
import data.Database;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class VisitRecord extends Model {

    private final SimpleIntegerProperty id = new SimpleIntegerProperty(this, "id", 0);
    private final SimpleObjectProperty<Patient> patient = new SimpleObjectProperty(this, "patient", null);
    private final SimpleStringProperty notes = new SimpleStringProperty(this, "notes", "");
    private final SimpleObjectProperty<LocalDateTime> visitDate = new SimpleObjectProperty(this, "visitDate", LocalDateTime.now());

    final ObservableList<Service> localServices = FXCollections.observableArrayList();
    ObservableList<Service> services;
    boolean servicesLoaded = false;

    boolean patientLoaded = false;

    boolean hasServicesPaidCalculated = false;

    boolean hasServicesPaid = false;

    public static VisitRecord NewInstance(SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener){
        VisitRecord v = new VisitRecord();
        v.setSaveListener(saveListener);
        v.setValidationListener(validationListener);
        v.setErrorListener(errorListener);
//        v.setDirty(true);
        v.isNew = true;
        return v;
    }

    public static VisitRecord FromDb(int id, String notes, LocalDateTime visitDate){
        VisitRecord v = new VisitRecord(id, notes, visitDate);
        return v;
    }

    public static VisitRecord FromDbPartial(int id, Patient patient){
        VisitRecord v = new VisitRecord(id, null, null);
        v.setPatient(patient);
        return v;
    }

    private VisitRecord(){
        services = FXCollections.unmodifiableObservableList(localServices);
        setRestoreBackupOnInvalid(false);
    }


    private VisitRecord(int id, String notes, LocalDateTime visitDate){
        this();
        setId(id);
        setNotes(notes);
        setVisitDate(visitDate);
    }


    public ObservableList<Service> getServices() {
        if(!servicesLoaded){
            loadServices();
            servicesLoaded = true;
        }
        return services;
    }

    private void loadServices() {
        ServiceService service = new ServiceService();
        localServices.setAll(service.getVisitServices(this));
    }

    public void addService(Service service){
        if(service == null) return;
        service.setVisitRecord(this);
        localServices.add(service);
    }

    public AdmissionRecord getAdmission(){
        AdmissionService service = new AdmissionService();
        return service.getVisitAdmission(this);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public Patient getPatient() {
        if(!patientLoaded){
            loadPatient();
            patientLoaded = true;
        }
        return patient.get();
    }

    private void loadPatient() {
        PatientService service = new PatientService();
        Patient p = service.getVisitingPatient(this);
        setPatient(p);
    }

    public SimpleObjectProperty<Patient> patientProperty() {
        return patient;
    }

    public void setPatient(Patient patient) {
        if(patient == null) return;
        this.patient.set(patient);
        patientLoaded = true;
    }

    public String getNotes() {
        return notes.get();
    }

    public SimpleStringProperty notesProperty() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public LocalDateTime getVisitDate() {
        return visitDate.get();
    }

    public SimpleObjectProperty<LocalDateTime> visitDateProperty() {
        return visitDate;
    }

    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate.set(visitDate);
    }

    public boolean hasServicesPaid(){
        Connection connection = Database.createConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select Count(s.Id) " +
                    "From Service s " +
                    "Join Visit v " +
                    "On s.VisitId = v.Id " +
                    "Where v.Id = ? And (s.Payment is null Or s.Payment = 0 Or (s.Payment - s.Fee) < 0)");
            statement.setInt(1, getId());
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                int unpaidServicesCount = resultSet.getInt(1);
                return unpaidServicesCount == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }
        return false;
    }

    private String notesBackup = null;

    @Override protected void backupFields() {
        notesBackup = getNotes();
    }

    @Override protected void restoreFields() {
        setNotes(notesBackup);
    }

    @Override protected void clearBackup() {
        notesBackup = null;
    }

    @Override
    protected void saveMethod() throws SQLException {
        Connection connection = Database.createConnection();
        PreparedStatement statement = connection.prepareStatement("Insert into Visit(PatientId, Notes, VisitDate) Values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, getPatient().getId());
        statement.setString(2, getNotes());
        statement.setTimestamp(3, Timestamp.valueOf(getVisitDate()));
        statement.executeUpdate();
        ResultSet resultSet = statement.getGeneratedKeys();
        if(resultSet.next()){
            int id = resultSet.getInt(1);
            setId(id);
        }
        Database.closeDatabaseResource(connection, statement, resultSet);
    }

    @Deprecated @Override protected void updateMethod() {

    }

    @Override protected void deleteMethod() {

    }

    @Override protected void refreshMethod() {
        VisitRecordService service = new VisitRecordService();
        VisitRecord v = service.getVisitRecordById(getId());
        if (v != null) {
            if(getPatient() == null) setPatient(v.getPatient());
            setNotes(v.getNotes());
            setVisitDate(v.getVisitDate());
        }
    }

    @Override protected boolean isIdentifiable() {
        return getId() > 0;
    }

    @Override protected AbstractList<ValidationCriterion> createValidationCriteria() {
        List<ValidationCriterion> validationCriteria = new ArrayList<>();

        validationCriteria.add(new ValidationCriterion("Reference to patient not found.") {
            @Override public boolean validate() {
                return getPatient() != null;
            }
        });

        validationCriteria.add(new ValidationCriterion("Reference to patient is invalid") {
            @Override public boolean validate() {
                return getPatient().isIdentifiable();
            }
        });

        validationCriteria.add(new ValidationCriterion("Visit notes contains brief summary of reason for patient visit and should not be empty.") {
            @Override public boolean validate() {
                return getNotes().trim().length() > 0;
            }
        });

        return new AbstractList<ValidationCriterion>() {
            @Override public ValidationCriterion get(int index) {
                return validationCriteria.get(index);
            }

            @Override public int size() {
                return validationCriteria.size();
            }
        };
    }
}
