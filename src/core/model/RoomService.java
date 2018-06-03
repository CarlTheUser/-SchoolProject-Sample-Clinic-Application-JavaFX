package core.model;

import data.Database;
import javafx.beans.property.SimpleObjectProperty;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class RoomService extends Service {
    private final SimpleObjectProperty<Room> room = new SimpleObjectProperty<>(this, "room", null);

    private final SimpleObjectProperty<LocalDateTime> dateIn = new SimpleObjectProperty<>(this, "dateIn", LocalDateTime.now());
    private final SimpleObjectProperty<LocalDateTime> dateOut = new SimpleObjectProperty<>(this, "dateOut", null);

    boolean roomLoaded = false;

    boolean feeCalculated = false;

    double calculatedFee = 0;

    public static RoomService FromDb(int id, VisitRecord visitRecord, String description, int serviceId, double fee, LocalDateTime serviceDate){
        RoomService r = new RoomService(visitRecord, null, null, null);
        r.setId(id);
        r.setDescription(description);
        r.setServiceId(serviceId);
        r.setFee(fee);
        r.setServieDate(serviceDate);
        r.isNew = false;
        return r;
    }

    protected RoomService(VisitRecord visitRecord, SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener) {
        super(visitRecord, LocalDateTime.now(), saveListener, validationListener, errorListener);
    }

    @Override
    public ServiceType getType() {
        return ServiceType.RoomService;
    }

    @Override
    public double calculateFee() {
        if(!feeCalculated) {
            refreshMethod();
            LocalDateTime dateOut = getDateOut();
            Duration duration = Duration.between(getDateIn(), dateOut == null ? LocalDateTime.now() : dateOut);
            long totalHours = duration.toHours() > 0 ? duration.toHours() : 1;
            calculatedFee = totalHours * getRoom().getHourlyRate();
            feeCalculated = true;
        }
        return calculatedFee;
    }

    @Override
    public ValidationCriterion isPayable() {
        return new ValidationCriterion("Room is still in use and time is applied. Go to Receptionist to log patient exit.") {
            @Override
            public boolean validate() {
                Connection connection = Database.createConnection();
                PreparedStatement statement = null;
                ResultSet resultSet = null;
                try {
                    statement = connection.prepareStatement("Select Id From RoomService Where Id = ? And DateOut is null");
                    statement.setInt(1, getServiceId());
                    resultSet = statement.executeQuery();
                    if(!resultSet.next()) return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            }
        };
    }

    public Room getRoom() {
        if(!roomLoaded){
            loadRoom();
            roomLoaded = true;
        }
        return room.get();
    }

    private void loadRoom() {
        core.service.RoomService service = new core.service.RoomService();
        setRoom(service.getRoomServiceRoom(this));
    }

    public SimpleObjectProperty<Room> roomProperty() {
        return room;
    }

    public void setRoom(Room room) {
        if(room == null) return;
        this.room.set(room);
        roomLoaded = true;
    }

    public LocalDateTime getDateIn() {
        return dateIn.get();
    }

    public SimpleObjectProperty<LocalDateTime> dateInProperty() {
        return dateIn;
    }

    public void setDateIn(LocalDateTime dateIn) {
        this.dateIn.set(dateIn);
    }

    public LocalDateTime getDateOut() {
        return dateOut.get();
    }

    public SimpleObjectProperty<LocalDateTime> dateOutProperty() {
        return dateOut;
    }

    public void setDateOut(LocalDateTime dateOut) {
        this.dateOut.set(dateOut);
    }

    private LocalDateTime dateInBackup = null;
    private LocalDateTime dateOutBackup = null;

    @Override
    protected void backupFields() {
        super.backupFields();
        dateInBackup = getDateIn();
        dateOutBackup = getDateOut();
    }

    @Override
    protected void restoreFields() {
        super.restoreFields();
        setDateIn(dateInBackup);
        setDateOut(dateOutBackup);
    }

    @Override
    protected void clearBackup() {
        super.clearBackup();
        dateOutBackup = null;
        dateOutBackup = null;
    }

    @Override
    protected void saveMethod() {

    }

    @Override
    protected void updateMethod() throws SQLException {
        Connection connection = Database.createConnection();
        PreparedStatement statement = connection.prepareStatement("Update RoomService Set DateIn = ?, DateOut = ? Where Id = ?");
        statement.setTimestamp(1, Timestamp.valueOf(getDateIn()));
        statement.setTimestamp(2, Timestamp.valueOf(getDateOut()));
        statement.setInt(3, getServiceId());
        statement.executeUpdate();
    }

    @Override
    protected void deleteMethod() {

    }

    @Override
    protected void refreshMethod() {
        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select rs.Id, rs.DateIn, rs.DateOut From RoomService rs join Service s On s.ServiceId = rs.Id Where s.Id = ?");
            statement.setInt(1, getId());
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                int serviceId = resultSet.getInt(1);
                LocalDateTime dateIn = resultSet.getTimestamp(2).toLocalDateTime();
                Timestamp timestamp = resultSet.getTimestamp(3);
                if(timestamp != null){
                    setDateOut(timestamp.toLocalDateTime());
                }
                setServiceId(serviceId);
                setDateIn(dateIn);
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

    }



    @Override
    protected AbstractList<ValidationCriterion> createValidationCriteria() {
        AbstractList<ValidationCriterion> superCriteria = super.createValidationCriteria();
        final List<ValidationCriterion> validationCriteria = new ArrayList<>();
        if(superCriteria != null) validationCriteria.addAll(superCriteria);
        validationCriteria.add(new ValidationCriterion("Room reference is not specified.") {
            @Override
            public boolean validate() {
                return room.get() != null;
            }
        });
        validationCriteria.add(new ValidationCriterion("Room reference is invalid.") {
            @Override
            public boolean validate() {
                return getRoom().isIdentifiable();
            }
        });
        return new AbstractList<>() {
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
