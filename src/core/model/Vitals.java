package core.model;

import core.util.NumberUtility;
import data.Database;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Vitals extends Model {

    public static final String NO_VALUE = "N/A";
    private final SimpleIntegerProperty id = new SimpleIntegerProperty(this, "id", 0);
    private final SimpleStringProperty bloodPresure = new SimpleStringProperty(this, "bloodPressure", NO_VALUE);
    private final SimpleStringProperty respiratoryRate = new SimpleStringProperty(this, "respiratoryRate", NO_VALUE);
    private final SimpleStringProperty weight = new SimpleStringProperty(this, "weight", NO_VALUE);
    private final SimpleStringProperty height = new SimpleStringProperty(this, "height", NO_VALUE);
    private final SimpleStringProperty temperature = new SimpleStringProperty(this, "temperature", NO_VALUE);
    private final SimpleObjectProperty<LocalDateTime> dateTaken = new SimpleObjectProperty<>(this, "dateTaken", null);

    public static Vitals NewInstance(Patient patient, SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener){
        Vitals v = new Vitals(patient.getId());
        v.setDateTaken(LocalDateTime.now());
        v.isNew = true;
        return v;
    }

    public static Vitals FromDb(int id, String bloodPressure, String respiratoryRate, String weight, String height, String temperature, LocalDateTime dateTaken){
        Vitals v = new Vitals(id, bloodPressure, respiratoryRate, weight, height, temperature, dateTaken);
        return v;
    }

    private Vitals(int id){
        setId(id);
        setRestoreBackupOnInvalid(true);
    }

    private Vitals(int id, String bloodPressure, String respiratoryRate, String weight, String height, String temperature, LocalDateTime dateTaken){
        setId(id);
        setBloodPresure(bloodPressure);
        setRespiratoryRate(respiratoryRate);
        setWeight(weight);
        setHeight(height);
        setTemperature(temperature);
        setDateTaken(dateTaken);
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

    public String getBloodPresure() {
        return bloodPresure.get();
    }

    public SimpleStringProperty bloodPresureProperty() {
        return bloodPresure;
    }

    public void setBloodPresure(String bloodPresure) {
        this.bloodPresure.set(bloodPresure);
    }

    public String getRespiratoryRate() {
        return respiratoryRate.get();
    }

    public SimpleStringProperty respiratoryRateProperty() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(String respiratoryRate) {
        this.respiratoryRate.set(respiratoryRate);
    }

    public String getWeight() {
        return weight.get();
    }

    public SimpleStringProperty weightProperty() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight.set(weight);
    }

    public String getHeight() {
        return height.get();
    }

    public SimpleStringProperty heightProperty() {
        return height;
    }

    public void setHeight(String height) {
        this.height.set(height);
    }

    public String getTemperature() {
        return temperature.get();
    }

    public SimpleStringProperty temperatureProperty() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature.set(temperature);
    }

    public LocalDateTime getDateTaken() {
        return dateTaken.get();
    }

    public SimpleObjectProperty<LocalDateTime> dateTakenProperty() {
        return dateTaken;
    }

    public void setDateTaken(LocalDateTime dateTaken) {
        this.dateTaken.set(dateTaken);
    }

    private String bloodPressureBackup = null;
    private String respiratoryRateBackup = null;
    private String weightBackup = null;
    private String heightBackup = null;
    private String temperatureBackup = null;

    @Override protected void backupFields() {
        bloodPressureBackup = getBloodPresure();
        respiratoryRateBackup = getRespiratoryRate();
        weightBackup = getWeight();
        heightBackup = getHeight();
        temperatureBackup = getTemperature();
    }

    @Override protected void restoreFields() {
        setBloodPresure(bloodPressureBackup);
        setRespiratoryRate(respiratoryRateBackup);
        setWeight(weightBackup);
        setHeight(heightBackup);
        setTemperature(temperatureBackup);
    }

    @Override protected void clearBackup() {
        bloodPressureBackup = null;
        respiratoryRateBackup = null;
        weightBackup = null;
        heightBackup = null;
        temperatureBackup = null;
    }

    @Override protected void saveMethod() throws SQLException {
        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        statement = connection.prepareStatement("Insert into Vitals(PatientId, BloodPressure, RespiratoryRate, Weight, Height, Temperature, DateTaken) Values (?, ?, ?, ?, ?, ?, ?)");

        statement.setInt(1, getId());
        statement.setString(2, getBloodPresure());
        statement.setString(3, getRespiratoryRate());
        statement.setString(4, getWeight());
        statement.setString(5, getHeight());
        statement.setString(6, getTemperature());
        statement.setTimestamp(7, Timestamp.valueOf(getDateTaken()));

        statement.executeUpdate();

        Database.closeDatabaseResource(connection, statement);
    }

    @Override protected void updateMethod() throws SQLException {
        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        statement = connection.prepareStatement("Update Vitals Set BloodPressure = ?, RespiratoryRate = ?, Weight = ?, Height = ?, Temperature = ?, DateTaken = ? Where PatientId = ?");

        statement.setString(1, getBloodPresure());
        statement.setString(2, getRespiratoryRate());
        statement.setString(3, getWeight());
        statement.setString(4, getHeight());
        statement.setString(5, getTemperature());
        statement.setTimestamp(6, Timestamp.valueOf(getDateTaken()));
        statement.setInt(7, getId());

        statement.executeUpdate();

        Database.closeDatabaseResource(connection, statement);
    }

    @Override protected void deleteMethod() throws Exception {

    }

    @Override protected void refreshMethod() throws Exception {

    }

    @Override protected boolean isIdentifiable() {
        return getId() > 0;
    }

    @Override protected AbstractList<ValidationCriterion> createValidationCriteria() {
        List<ValidationCriterion> validationCriteria = new ArrayList<>();

        validationCriteria.add(new ValidationCriterion("Vitals record isn't associated with a patient.") {
            @Override public boolean validate() {
                return isIdentifiable();
            }
        });

        validationCriteria.add(new ValidationCriterion("Blood pressure is blank.") {
            @Override public boolean validate() {
                return getBloodPresure().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Blood pressure should include numerical elements") {
            @Override public boolean validate() {
                return NumberUtility.containsNumber(getBloodPresure());
            }
        });

        validationCriteria.add(new ValidationCriterion("Respiratory rate is blank.") {
            @Override public boolean validate() {
                return getRespiratoryRate().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Respiratory rate should include numerical elements") {
            @Override public boolean validate() {
                return NumberUtility.containsNumber(getRespiratoryRate());
            }
        });

        validationCriteria.add(new ValidationCriterion("Weight rate is blank.") {
            @Override public boolean validate() {
                return getWeight().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Weight should include numerical elements") {
            @Override public boolean validate() {
                return NumberUtility.containsNumber(getWeight());
            }
        });

        validationCriteria.add(new ValidationCriterion("Height is blank.") {
            @Override public boolean validate() {
                return getHeight().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Height should include numerical elements") {
            @Override public boolean validate() {
                return NumberUtility.containsNumber(getHeight());
            }
        });

        validationCriteria.add(new ValidationCriterion("Temperature is blank.") {
            @Override public boolean validate() {
                return getTemperature().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Temperature should include numerical elements") {
            @Override public boolean validate() {
                return NumberUtility.containsNumber(getTemperature());
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
