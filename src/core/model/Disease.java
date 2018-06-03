package core.model;

import data.Database;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Disease extends Model implements BatchSaveable{

    private final SimpleIntegerProperty id = new SimpleIntegerProperty(this, "id", 0);
    private final SimpleStringProperty name = new SimpleStringProperty(this, "name", "");
    private final SimpleStringProperty description = new SimpleStringProperty(this, "description", "");
    private final SimpleStringProperty status = new SimpleStringProperty(this, "Status", "");
    private final SimpleObjectProperty<Patient> patient = new SimpleObjectProperty<>(this, "patient", null);

    public static Disease NewInstance(SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener){
        Disease d = new Disease();
        d.setSaveListener(saveListener);
        d.setValidationListener(validationListener);
        d.setErrorListener(errorListener);
//        d.setDirty(true);
        d.isNew = true;
        return d;
    }

    public static Disease FromDb(int id, String name, String description, String status) {
        Disease d = new Disease(id, name, description, status);
        return d;
    }

    private Disease(){ setRestoreBackupOnInvalid(true); }

    private Disease(int id, String name, String description, String status){
        setId(id);
        setName(name);
        setDescription(description);
        setStatus(status);
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

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
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
    private String descriptionBackup = null;
    private String statusBackup = null;


    @Override protected void backupFields() {
        nameBackup = getName();
        descriptionBackup = getDescription();
        statusBackup = getStatus();
    }

    @Override protected void restoreFields() {
        setName(nameBackup);
        setDescription(descriptionBackup);
        setStatus(statusBackup);
    }

    @Override
    protected void clearBackup() {
        nameBackup = null;
        descriptionBackup = null;
        statusBackup = null;
    }

    @Override protected void saveMethod() throws SQLException{
        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("Insert into PatientCondition(PatientId, Name, Description, Status) Values (?, ?, ?, ?)",  Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, getPatient().getId());
            statement.setString(2, getName());
            statement.setString(3, getDescription());
            statement.setString(4, getStatus());

            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            if(resultSet.next()){
                int id = resultSet.getInt(1);
                setId(id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            Database.closeDatabaseResource(connection, statement);
        }
    }

    @Override protected void updateMethod() throws SQLException {
        Connection connection = Database.createConnection();

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("Update PatientCondition Set Name = ?, Description = ?, Status = ? Where Id = ?");
            statement.setString(1, getName());
            statement.setString(2, getDescription());
            statement.setString(3, getStatus());
            statement.setInt(4, getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            Database.closeDatabaseResource(connection, statement);
        }

    }

    @Override protected void deleteMethod() {

    }

    @Override protected void refreshMethod() {

    }

    @Override protected boolean isIdentifiable() {
        return getId() > 0;
    }

    @Override protected AbstractList<ValidationCriterion> createValidationCriteria() {
        List<ValidationCriterion> validationCriteria = new ArrayList<>();
        validationCriteria.add(new ValidationCriterion("Condition name is required.") {
            @Override
            public boolean validate() {
                return getName().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Condition status is required.") {
            @Override
            public boolean validate() {
                return getStatus().trim().length() > 0;
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

        if(!isNew){
            validationCriteria.add(new ValidationCriterion("entity key not found.") {
                @Override
                public boolean validate() {
                    return isIdentifiable();
                }
            });
        }

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
