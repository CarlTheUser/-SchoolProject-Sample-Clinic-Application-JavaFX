package core.model;

import core.util.NumberUtility;
import data.Database;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class ContactPerson extends Model implements BatchSaveable{

    private final SimpleIntegerProperty id = new SimpleIntegerProperty(this, "id", 0);
    private final SimpleStringProperty name = new SimpleStringProperty(this, "name", "");
    private final SimpleStringProperty contactNumber = new SimpleStringProperty(this, "contactNumber", "");
    private final SimpleStringProperty address = new SimpleStringProperty(this, "address", "");
    private final SimpleStringProperty relation = new SimpleStringProperty(this, "relation", "");
    private final SimpleObjectProperty<Patient> patient = new SimpleObjectProperty<>(this, "patient", null);

    public static ContactPerson NewInstance(SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener){
        ContactPerson c = new ContactPerson();
        c.setSaveListener(saveListener);
        c.setValidationListener(validationListener);
        c.setErrorListener(errorListener);
//        c.setDirty(true);
        c.isNew = true;
        return c;
    }

    public static ContactPerson FromDb(int id, String name, String contactNumber, String address, String relation) {
        ContactPerson c = new ContactPerson(id, name, contactNumber, address, relation);
        return c;
    }

    private ContactPerson() { setRestoreBackupOnInvalid(true); }

    private ContactPerson(int id, String name, String contactNumber, String address, String relation){
        setId(id);
        setName(name);
        setContactNumber(contactNumber);
        setAddress(address);
        setRelation(relation);
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

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
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

    public String getAddress() {
        return address.get();
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getRelation() {
        return relation.get();
    }

    public SimpleStringProperty relationProperty() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation.set(relation);
    }

    public Patient getPatient() {
        return patient.get();
    }

    public SimpleObjectProperty<Patient> patientProperty() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient.set(patient);
    }

    private String nameBackup = null;
    private String contactNumberBackup = null;
    private String addressBackup = null;
    private String relationBackup = null;

    @Override
    protected void backupFields() {
        nameBackup = getName();
        contactNumberBackup = getContactNumber();
        addressBackup = getAddress();
        relationBackup = getRelation();
    }

    @Override
    protected void restoreFields() {
        setName(nameBackup);
        setContactNumber(contactNumberBackup);
        setAddress(addressBackup);
        setRelation(relationBackup);
    }

    @Override
    protected void clearBackup() {
        nameBackup = null;
        contactNumberBackup = null;
        addressBackup = null;
        relationBackup = null;
    }

    @Override
    protected void saveMethod() throws SQLException {
        Connection connection = Database.createConnection();
        PreparedStatement statement = connection.prepareStatement("Insert into ContactPerson(PatientId, Name, ContactNumber, Address, Relation) Values(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, getPatient().getId());
        statement.setString(2, getName());
        statement.setString(3, getContactNumber());
        statement.setString(4, getAddress());
        statement.setString(5, getRelation());
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
        PreparedStatement statement = connection.prepareStatement("Update ContactPerson Set Name = ?, ContactNumber = ?, Address = ?, Relation = ? Where Id = ?");
        statement.setString(1, getName());
        statement.setString(2, getContactNumber());
        statement.setString(3, getAddress());
        statement.setString(4, getRelation());
        statement.setInt(5, getId());
        statement.executeUpdate();
        Database.closeDatabaseResource(connection, statement);
    }

    @Override
    protected void deleteMethod() {

    }

    @Override
    protected void refreshMethod() {

    }

    @Override
    protected boolean isIdentifiable() {
        return getId() > 0;
    }

    @Override
    protected AbstractList<ValidationCriterion> createValidationCriteria() {
        List<ValidationCriterion> validationCriteria = new ArrayList<>();
        validationCriteria.add(new ValidationCriterion("Contact name is too short.") {
            @Override
            public boolean validate() {
                return getName().trim().length() > 5;
            }
        });

        validationCriteria.add(new ValidationCriterion("Contact number should only contain numbers.") {
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


        validationCriteria.add(new ValidationCriterion("Address is not provided.") {
            @Override
            public boolean validate() {
                return getAddress().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Address is too short.") {
            @Override
            public boolean validate() {
                return getAddress().trim().length() > 5;
            }
        });

        validationCriteria.add(new ValidationCriterion("Relation to patient is not defined.") {
            @Override
            public boolean validate() {
                return getRelation().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Reference to patient not found.") {
            @Override
            public boolean validate() {
                return getPatient() != null;
            }
        });

        validationCriteria.add(new ValidationCriterion("Missing key for patient reference.") {
            @Override
            public boolean validate() {
                    return getPatient().isIdentifiable();
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

    @Override
    public void onBatchSaved() {
        isNew = false;
    }
}
