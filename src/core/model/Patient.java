package core.model;

import core.service.*;
import core.util.NumberUtility;
import data.Database;
import core.model.Vitals;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Patient extends Model{

    final private SimpleIntegerProperty id = new SimpleIntegerProperty(this, "id", 0);
    final private SimpleStringProperty firstname = new SimpleStringProperty(this, "firstname", "");
    final private SimpleStringProperty middlename = new SimpleStringProperty(this, "middlename", "");
    final private SimpleStringProperty lastname = new SimpleStringProperty(this, "lastname", "");
    final private SimpleObjectProperty<LocalDate> birthdate = new SimpleObjectProperty<>(this, "birthdate", LocalDate.now());
    final private SimpleObjectProperty<Gender> gender = new SimpleObjectProperty<>(this, "gender", Gender.None);
    final private SimpleStringProperty address = new SimpleStringProperty(this, "address", "");
    final private SimpleStringProperty contactNumber = new SimpleStringProperty(this, "contactNumber", "");
    final private SimpleStringProperty nationality = new SimpleStringProperty(this, "nationality", "");
    final private SimpleStringProperty religion = new SimpleStringProperty(this, "religion", "");
    final private ReadOnlyIntegerWrapper age;
    final private ReadOnlyStringWrapper fullname;

    private final ObservableList<ContactPerson> contactPeople;
    private final ObservableList<ContactPerson> localContactPeople;
    private boolean contactsLoaded = false;

    private final ObservableList<Disease> conditions;
    private final ObservableList<Disease> localConditions;
    private boolean conditionsLoaded = false;

    private final ObservableList<VisitRecord> visitRecords;
    private final ObservableList<VisitRecord> localVisitRecords;
    private boolean visitRecordsLoaded = false;

    private Vitals vitals = null;
    private boolean vitalsLoaded = false;

    private final ObservableList<Service> unpaidServices;
    private boolean unpaidServicesLoaded = false;

    public static Patient NewInstance(SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener){
        Patient p = new Patient();
        p.setSaveListener(saveListener);
        p.setValidationListener(validationListener);
        p.setErrorListener(errorListener);
//        p.setDirty(true);
        p.isNew = true;
        return p;
    }

    public static Patient FromDb(int id, String firstname, String middlename, String lastname, Gender gender, LocalDate birthdate, String contactNumber, String address, String nationiality, String religion){
        Patient p = new Patient(id, firstname, middlename, lastname, gender, birthdate, contactNumber, address, nationiality, religion);
        p.isNew = false;
        return p;
    }

    public static Patient FromDbPartial(int id, String firstname, String middlename, String lastname){
        return FromDb(id, firstname, middlename, lastname, null, null, null, null, null, null);
    }

    private Patient(){
        age = new ReadOnlyIntegerWrapper(this, "age");
        fullname = new ReadOnlyStringWrapper(this, "fullname");
        age.bind(Bindings.createIntegerBinding(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return Period.between(Patient.this.getBirthdate(), LocalDate.now()).getYears();
            }
        },birthdateProperty()));
        fullname.bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String fullname = "";
                fullname += Patient.this.firstname.getValue() + " ";
                if(Patient.this.middlename.getValue().length() > 0) fullname += Patient.this.middlename.getValue() + " ";
                fullname += Patient.this.lastname.getValue();
                return fullname;
            }
        }, firstnameProperty(), middlenameProperty(), lastnameProperty()));

        localContactPeople = FXCollections.observableArrayList();
        contactPeople = FXCollections.unmodifiableObservableList(localContactPeople);

        localConditions = FXCollections.observableArrayList();
        conditions = FXCollections.unmodifiableObservableList(localConditions);

        localVisitRecords = FXCollections.observableArrayList();
        visitRecords = FXCollections.unmodifiableObservableList(localVisitRecords);

        unpaidServices = FXCollections.observableArrayList();
        setRestoreBackupOnInvalid(false);
    }

    private Patient(int id, String firstname, String middlename, String lastname, Gender gender, LocalDate birthdate, String contactNumber, String address, String nationiality, String religion){
        this();
        setId(id);
        setFirstname(firstname);
        setMiddlename(middlename);
        setLastname(lastname);
        setGender(gender);
        setBirthdate(birthdate);
        setContactNumber(contactNumber);
        setAddress(address);
        setNationality(nationiality);
        setReligion(religion);
    }


    public ObservableList<Service> getUnpaidServices() {
        if(!isNew && !unpaidServicesLoaded){
            loadUnpaidServices();
            unpaidServicesLoaded = true;
        }
        return unpaidServices;
    }

    public ObservableList<ContactPerson> getContactPeople() {
        if(!isNew && !contactsLoaded){
            loadContacts();
            contactsLoaded = true;
        }
        return contactPeople;
    }

    public ObservableList<Disease> getConditions() {
        if(!isNew && !conditionsLoaded){
            loadConditions();
            conditionsLoaded = true;
        }
        return conditions;
    }

    public ObservableList<VisitRecord> getVisitRecords() {
        if(!isNew && !visitRecordsLoaded){
            loadVisitRecords();
            visitRecordsLoaded = true;
        }
        return visitRecords;
    }

    public void addContactPerson(ContactPerson contactPerson){
        contactPerson.setPatient(this);
        localContactPeople.add(contactPerson);
    }

    public void addCondition(Disease disease){
        disease.setPatient(this);
        localConditions.add(disease);
    }

    public void addVisitRecord(VisitRecord visitRecord){
        visitRecord.setPatient(this);
        localVisitRecords.add(visitRecord);
    }

    public Vitals getVitals() {
        if(!vitalsLoaded){
            loadVitals();
            vitalsLoaded = true;
        }
        return vitals;
    }

    public void setVitals(Vitals vitals) {
        if(vitals == null) return;
        this.vitals = vitals;
        this.vitals.setId(getId());
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

    public String getFirstname() {
        return firstname.get();
    }

    public SimpleStringProperty firstnameProperty() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname.set(firstname);
    }

    public String getMiddlename() {
        return middlename.get();
    }

    public SimpleStringProperty middlenameProperty() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename.set(middlename);
    }

    public String getLastname() {
        return lastname.get();
    }

    public SimpleStringProperty lastnameProperty() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname.set(lastname);
    }

    public LocalDate getBirthdate() {
        return birthdate.get();
    }

    public SimpleObjectProperty<LocalDate> birthdateProperty() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate.set(birthdate);
    }

    public Gender getGender() {
        return gender.get();
    }

    public SimpleObjectProperty<Gender> genderProperty() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender.set(gender);
    }

    public String getAddress() {
        return address.get();
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getContactNumber() {
        return contactNumber.get();
    }

    public SimpleStringProperty contactNumberProperty() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber.set(contactNumber);
    }

    public String getNationality() {
        return nationality.get();
    }

    public SimpleStringProperty nationalityProperty() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality.set(nationality);
    }

    public String getReligion() {
        return religion.get();
    }

    public SimpleStringProperty religionProperty() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion.set(religion);
    }

    public int getAge() {
        return age.get();
    }

    public ReadOnlyIntegerProperty ageProperty() {
        return age.getReadOnlyProperty();
    }

    public String getFullname() {
        return fullname.get();
    }

    public ReadOnlyStringProperty fullnameProperty() {
        return fullname.getReadOnlyProperty();
    }

    private void loadVitals(){
        VitalsService service = new VitalsService();
        Vitals v = service.getPatientVitals(this);
        setVitals(v);
    }

    private void loadUnpaidServices() {
        ServiceService service = new ServiceService();
        unpaidServices.setAll(service.getPatientUnpaidServices(this));
    }

    private void loadContacts(){
        ContactPersonService contactPersonService = new ContactPersonService();
        localContactPeople.setAll(contactPersonService.getPatientContacts(this));
    }

    private void loadConditions(){
        DiseaseService diseaseService = new DiseaseService();
        localConditions.setAll(diseaseService.getPatientConditions(this));
    }

    private void loadVisitRecords(){
        VisitRecordService service = new VisitRecordService();
        localVisitRecords.setAll(service.getVisitRecordsByPatient(this));
    }

    private String firstnameBackup = null;
    private String middlenameBackup = null;
    private String lastnameBackup = null;
    private LocalDate birthdateBackup = null;
    private String addressBackup = null;
    private String contactNumberBackup = null;
    private Gender genderBackup = null;
    private String nationalityBackup = null;
    private String religionBackup = null;

    @Override
    protected void backupFields() {
        firstnameBackup = getFirstname();
        middlenameBackup = getMiddlename();
        lastnameBackup = getLastname();
        birthdateBackup = getBirthdate();
        addressBackup = getAddress();
        contactNumberBackup = getContactNumber();
        genderBackup =getGender();
        nationalityBackup = getNationality();
        religionBackup = getReligion();
    }

    @Override
    protected void restoreFields() {
        setFirstname(firstnameBackup);
        setMiddlename(middlenameBackup);
        setLastname(lastnameBackup);
        setBirthdate(birthdateBackup);
        setAddress(addressBackup);
        setContactNumber(contactNumberBackup);
        setGender(genderBackup);
        setNationality(nationalityBackup);
        setReligion(religionBackup);
    }

    @Override
    protected void clearBackup() {
        firstnameBackup = null;
        middlenameBackup = null;
        lastnameBackup = null;
        birthdateBackup = null;
        addressBackup = null;
        contactNumberBackup = null;
        genderBackup = null;
        nationalityBackup = null;
        religionBackup = null;
    }

    @Override
    protected void saveMethod() throws SQLException {
        Connection connection = Database.createConnection();
        PreparedStatement statement = connection.prepareStatement("Insert into Patient(Firstname, Middlename, Lastname, Gender, Birthdate, ContactNumber, Address, Nationality, Religion) Values(?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, getFirstname());
        statement.setString(2, getMiddlename());
        statement.setString(3, getLastname());
        statement.setInt(4, getGender().getValue());
        statement.setDate(5, Date.valueOf(getBirthdate()));
        statement.setString(6, getContactNumber());
        statement.setString(7, getAddress());
        statement.setString(8, getNationality());
        statement.setString(9, getReligion());
        statement.executeUpdate();
        ResultSet resultSet = statement.getGeneratedKeys();
        if(resultSet.next()){
            int id = resultSet.getInt(1);
            setId(id);
        }
        Database.closeDatabaseResource(connection, statement);
    }

    @Override
    protected void updateMethod() throws SQLException {
        Connection connection = Database.createConnection();
        PreparedStatement statement = connection.prepareStatement("Update Patient set Firstname = ?, Middlename = ?, Lastname = ?, Gender = ?, Birthdate = ?, ContactNumber = ?, Address = ?, Nationality = ?, Religion = ? Where Id = ?");
        statement.setString(1, getFirstname());
        statement.setString(2, getMiddlename());
        statement.setString(3, getLastname());
        statement.setInt(4, getGender().getValue());
        statement.setDate(5, Date.valueOf(getBirthdate()));
        statement.setString(6, getContactNumber());
        statement.setString(7, getAddress());
        statement.setString(8, getNationality());
        statement.setString(9, getReligion());
        statement.setInt(10, getId());
        statement.executeUpdate();
        Database.closeDatabaseResource(connection, statement);
    }

    @Override
    protected void deleteMethod() {

    }

    @Override
    protected void refreshMethod() {
        if(isIdentifiable()){
            PatientService service = new PatientService();
            Patient p = service.getPatientById(getId());
            setFirstname(p.getFirstname());
            setMiddlename(p.getMiddlename());
            setLastname(p.getLastname());
            setGender(p.getGender());
            setBirthdate(p.getBirthdate());
            setContactNumber(p.getContactNumber());
            setAddress(p.getAddress());
            setNationality(p.getNationality());
            setReligion(p.getReligion());
            p = null;
        }
    }

    @Override
    protected boolean isIdentifiable() {
        return getId() > 0;
    }

    @Override
    protected AbstractList<ValidationCriterion> createValidationCriteria() {
        List<ValidationCriterion> validationCriteria = new ArrayList<>();

        validationCriteria.add(new ValidationCriterion("Patient firstname is required.") {
            @Override
            public boolean validate() {
                return getFirstname().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Patient lastname is required.") {
            @Override
            public boolean validate() {
                return getLastname().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Patient can't be born on a latter time.") {
            @Override
            public boolean validate() {
                LocalDate now = LocalDate.now();
                LocalDate birthdate = getBirthdate();
                return birthdate.isEqual(now) || birthdate.isBefore(now);
            }
        });

        validationCriteria.add(new ValidationCriterion("Patient gender is null.") {
            @Override
            public boolean validate() {
                return getGender() != null;
            }
        });

        validationCriteria.add(new ValidationCriterion("Patient gender is not supplied.") {
            @Override
            public boolean validate() {
                return getGender() != Gender.None;
            }
        });

        validationCriteria.add(new ValidationCriterion("Patient address is not supplied.") {
            @Override
            public boolean validate() {
                return getAddress().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Address supplied is too short.") {
            @Override
            public boolean validate() {
                return getAddress().trim().length() > 5;
            }
        });

        validationCriteria.add(new ValidationCriterion("Patient contact number is required.") {
            @Override
            public boolean validate() {
                return getContactNumber().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Contact number must be numeric.") {
            @Override
            public boolean validate() {
                return NumberUtility.isNumber(getContactNumber());
            }
        });

        validationCriteria.add(new ValidationCriterion("Contact number is too short.") {
            @Override
            public boolean validate() {
                return getContactNumber().trim().length() >= 7;
            }
        });

        return new AbstractList<ValidationCriterion>() {
            @Override
            public ValidationCriterion get(int index) {
                return validationCriteria.get(index);
            }

            @Override
            public int size() {
                return validationCriteria.size();
            }
        };
    }
}
