package core.model;

import data.Database;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class Room extends Model{

    final SimpleIntegerProperty id = new SimpleIntegerProperty(this, "id", 0);
    final SimpleObjectProperty<RoomType> type = new SimpleObjectProperty<>(this, "type", RoomType.Unknown);
    final SimpleIntegerProperty bedCount = new SimpleIntegerProperty(this, "bedCount", 0);
    final SimpleDoubleProperty hourlyRate = new SimpleDoubleProperty(this, "hourlyRate", 0);
    final SimpleStringProperty LocationDetails = new SimpleStringProperty(this, "locationDetails", "");

    public static Room NewInstance(SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener){
        Room r = new Room();
        r.setSaveListener(saveListener);
        r.setValidationListener(validationListener);
        r.setErrorListener(errorListener);
        r.isNew = true;
        return r;
    }

    public static Room FromDb(int id, RoomType type, int bedCount, double hourlyRate, String locationDetails){
        Room r = new Room(id, type, bedCount, hourlyRate, locationDetails);
        r.isNew = false;
        return r;
    }

    public static Room FromDbPartial(int id, String locationDetails){
        return FromDb(id, null, 0, 0, locationDetails);
    }

    private Room(int id, RoomType type, int bedCount, double hourlyRate, String locationDetails){
        setId(id);
        setType(type);
        setBedCount(bedCount);
        setHourlyRate(hourlyRate);
        setLocationDetails(locationDetails);
    }

    private Room() { setRestoreBackupOnInvalid(false); }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public RoomType getType() {
        return type.get();
    }

    public SimpleObjectProperty<RoomType> typeProperty() {
        return type;
    }

    public void setType(RoomType type) {
        this.type.set(type);
    }

    public int getBedCount() {
        return bedCount.get();
    }

    public SimpleIntegerProperty bedCountProperty() {
        return bedCount;
    }

    public void setBedCount(int bedCount) {
        this.bedCount.set(bedCount);
    }

    public double getHourlyRate() {
        return hourlyRate.get();
    }

    public SimpleDoubleProperty hourlyRateProperty() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate.set(hourlyRate);
    }

    public String getLocationDetails() {
        return LocationDetails.get();
    }

    public SimpleStringProperty locationDetailsProperty() {
        return LocationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.LocationDetails.set(locationDetails);
    }

    private RoomType roomTypeBackup = null;
    private Integer bedCountBackup = null;
    private Double hourlyRateBackup = null;
    private String locationDetailsBackup = null;

    @Override
    protected void backupFields() {
        roomTypeBackup = getType();
        bedCountBackup = getBedCount();
        hourlyRateBackup = getHourlyRate();
        locationDetailsBackup = getLocationDetails();
    }

    @Override
    protected void restoreFields() {
        setType(roomTypeBackup);
        setBedCount(bedCountBackup);
        setHourlyRate(hourlyRateBackup);
        setLocationDetails(locationDetailsBackup);
    }

    @Override
    protected void clearBackup() {
        roomTypeBackup = null;
        bedCountBackup = null;
        hourlyRateBackup = null;
        locationDetailsBackup = null;
    }

    @Override
    protected void saveMethod() throws SQLException {
        Connection connection = Database.createConnection();
        PreparedStatement statement = connection.prepareStatement("Insert into Room(RoomType, HourlyRate, BedCount, LocationDetails) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, getType().getValue());
        statement.setDouble(2, getHourlyRate());
        statement.setInt(3, getBedCount());
        statement.setString(4, getLocationDetails());
        statement.executeUpdate();
        ResultSet resultSet = statement.getGeneratedKeys();
        if(resultSet.next()){
            int id = resultSet.getInt(1);
            setId(id);
        }
        Database.closeDatabaseResource(connection, statement);
    }

    @Override
    protected void updateMethod() throws SQLException{
        Connection connection = Database.createConnection();
        PreparedStatement statement = connection.prepareStatement("update Room Set RoomType = ?, HourlyRate = ?, BedCount = ?, LocationDetails = ? Where Id = ?");
        statement.setInt(1, getType().getValue());
        statement.setDouble(2, getHourlyRate());
        statement.setInt(3, getBedCount());
        statement.setString(4, getLocationDetails());
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

        validationCriteria.add(new ValidationCriterion("Room type is not specified.") {
            @Override
            public boolean validate() {
                return getType() != RoomType.Unknown;
            }
        });

        validationCriteria.add(new ValidationCriterion("Room for patients must have bed.") {
            @Override
            public boolean validate() {
                return getBedCount() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Enter a value for hourly rate.") {
            @Override
            public boolean validate() {
                return getHourlyRate() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Room location details is required for guiding patients direction to the room.") {
            @Override
            public boolean validate() {
                return getLocationDetails().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Room location details is not descriptive enough.") {
            @Override
            public boolean validate() {
                return getLocationDetails().trim().length() > 7;
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
