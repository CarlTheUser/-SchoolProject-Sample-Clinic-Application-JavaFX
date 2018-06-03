package core.model;

import core.service.RoomService;
import data.Database;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class AdmissionRecord extends Model{

    private final SimpleObjectProperty<VisitRecord> visit = new SimpleObjectProperty<>(this, "visit", null);
    private final SimpleStringProperty initialFindings = new SimpleStringProperty(this, "initialFindings", "");
    private final SimpleStringProperty finalFindings = new SimpleStringProperty(this, "finalFindings", "");
    private final SimpleObjectProperty<LocalDateTime> admissonDate = new SimpleObjectProperty<LocalDateTime>(this, "admissionDate", null);
    private final SimpleObjectProperty<LocalDateTime> dischargeDate = new SimpleObjectProperty<LocalDateTime>(this, "dischargeDate", null);
    private final SimpleObjectProperty<Room> room = new SimpleObjectProperty<>(this, "room", null);

    public static AdmissionRecord NewInstance(VisitRecord visitRecord, SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener){
        AdmissionRecord a = new AdmissionRecord();
        a.setVisit(visitRecord);
        a.setSaveListener(saveListener);
        a.setValidationListener(validationListener);
        a.setErrorListener(errorListener);
//        v.setDirty(true);
        a.isNew = true;
        return a;
    }

    public static AdmissionRecord FromDb(VisitRecord visitRecord, String initialFindings, String finalFindings, LocalDateTime admissionDate, LocalDateTime dischargeDate){
        AdmissionRecord a = new AdmissionRecord(visitRecord, initialFindings, finalFindings, admissionDate, dischargeDate);
        return a;
    }

    public static AdmissionRecord FromDbPartial(VisitRecord visitRecord, Room room, String initialFindings, LocalDateTime admissionDate){
        AdmissionRecord a = FromDb(visitRecord, initialFindings, null, admissionDate, null);
        a.setRoom(room);
        return a;
    }

    private AdmissionRecord(){
        setRestoreBackupOnInvalid(false);
    }

    private AdmissionRecord(VisitRecord visitRecord, String initialFindings, String finalFindings, LocalDateTime admissionDate, LocalDateTime dischargeDate){
        setVisit(visitRecord);
        setInitialFindings(initialFindings);
        setFinalFindings(finalFindings);
        setAdmissonDate(admissionDate);
        setDischargeDate(dischargeDate);
    }

    public void discharge(){

    }

    private boolean isRoomLoaded = false;

    private void loadRoom(){
        RoomService service = new RoomService();
        setRoom(service.getCurrentRoom(getVisit()));
    }

    public VisitRecord getVisit() {
        return visit.get();
    }

    public SimpleObjectProperty<VisitRecord> visitProperty() {
        return visit;
    }

    public void setVisit(VisitRecord visit) {
        if(visit == null) return;
        this.visit.set(visit);
    }

    public String getInitialFindings() {
        return initialFindings.get();
    }

    public SimpleStringProperty initialFindingsProperty() {
        return initialFindings;
    }

    public void setInitialFindings(String initialFindings) {
        this.initialFindings.set(initialFindings);
    }

    public String getFinalFindings() {
        return finalFindings.get();
    }

    public SimpleStringProperty finalFindingsProperty() {
        return finalFindings;
    }

    public void setFinalFindings(String finalFindings) {
        this.finalFindings.set(finalFindings);
    }

    public LocalDateTime getAdmissonDate() {
        return admissonDate.get();
    }

    public SimpleObjectProperty<LocalDateTime> admissonDateProperty() {
        return admissonDate;
    }

    public void setAdmissonDate(LocalDateTime admissonDate) {
        this.admissonDate.set(admissonDate);
    }

    public LocalDateTime getDischargeDate() {
        return dischargeDate.get();
    }

    public SimpleObjectProperty<LocalDateTime> dischargeDateProperty() {
        return dischargeDate;
    }

    public void setDischargeDate(LocalDateTime dischargeDate) {
        this.dischargeDate.set(dischargeDate);
    }

    public Room getCurrentRoom() {
        if(!isNew && !isRoomLoaded){
            loadRoom();
            isRoomLoaded = true;
        }
        return room.get();
    }

    public SimpleObjectProperty<Room> roomProperty() {
        return room;
    }

    public void setRoom(Room room) {
        if(room == null) return;
        this.room.set(room);
        isRoomLoaded = true;
    }

    private String initialFindingsBackup = null;
    private LocalDateTime admissionDateBackup = null;

    @Override
    protected void backupFields() {
        initialFindingsBackup = getInitialFindings();
        admissionDateBackup = getAdmissonDate();
    }

    @Override
    protected void restoreFields() {
        setInitialFindings(initialFindingsBackup);
        setAdmissonDate(admissionDateBackup);
    }

    @Override
    protected void clearBackup() {
        initialFindingsBackup = null;
        admissionDateBackup = null;
    }

    @Override
    protected void saveMethod() throws SQLException {
        Connection connection = Database.createConnection();
        connection.setAutoCommit(false);
        try {

            PreparedStatement statement = connection.prepareStatement("Insert into Admission(VisitId, InitialFindings, AdmissionDate) Values(?, ?, ?)");
            statement.setInt(1, getVisit().getId());
            statement.setString(2, getInitialFindings());
            statement.setTimestamp(3, Timestamp.valueOf(getAdmissonDate()));
            statement.executeUpdate();

            PreparedStatement roomServiceStatement = connection.prepareStatement("Insert Into RoomService(RoomId, DateIn) Values(?, ?)", Statement.RETURN_GENERATED_KEYS);
            roomServiceStatement.setInt(1, getCurrentRoom().getId());
            roomServiceStatement.setTimestamp(2, Timestamp.valueOf(getAdmissonDate()));
            roomServiceStatement.executeUpdate();

            ResultSet resultSet = roomServiceStatement.getGeneratedKeys();
            int serviceId = 0;
            if(resultSet.next()){
                serviceId = resultSet.getInt(1);
            }

            PreparedStatement serviceStatement = connection.prepareStatement("Insert Into Service(VisitId, ServiceType, Description, ServiceId, Fee, ServiceDate) Values(?, ?, ?, ?, ?, ?)");

            serviceStatement.setInt(1, getVisit().getId());
            serviceStatement.setInt(2, ServiceType.RoomService.getValue());
            serviceStatement.setString(3, "Admission room");
            serviceStatement.setInt(4, serviceId);
            serviceStatement.setDouble(5, 0);
            serviceStatement.setTimestamp(6, Timestamp.valueOf(getAdmissonDate()));

            serviceStatement.executeUpdate();

            connection.commit();

        } catch (Exception e){
            connection.rollback();
            throw e;
        }finally {
            Database.closeDatabaseResource(connection);
        }



    }

    @Override
    protected void updateMethod() throws SQLException{
        Connection connection = Database.createConnection();
        PreparedStatement statement = connection.prepareStatement("Update Admission Set FinalFindings = ?, DischargeDate = ? Where VisitId = ?");
        statement.setString(1, getFinalFindings());
        statement.setTimestamp(2, Timestamp.valueOf(getDischargeDate()));
        statement.setInt(3, getVisit().getId());
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
        return getVisit() != null && getVisit().isIdentifiable();
    }

    @Override
    protected AbstractList<ValidationCriterion> createValidationCriteria() {
        List<ValidationCriterion> validationCriteria = new ArrayList<>();

        validationCriteria.add(new ValidationCriterion("Reference to patient visit not found.") {
            @Override
            public boolean validate() {
                return getVisit() != null;
            }
        });

        validationCriteria.add(new ValidationCriterion("Missing key for patient visit.") {
            @Override
            public boolean validate() {
                return getVisit().isIdentifiable();
            }
        });

        validationCriteria.add(new ValidationCriterion("Initial findings is blank.") {
            @Override
            public boolean validate() {
                return getInitialFindings().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Initial findings is too short.") {
            @Override
            public boolean validate() {
                return getInitialFindings().trim().length() > 3;
            }
        });

        validationCriteria.add(new ValidationCriterion("Reference to room not found.") {
            @Override
            public boolean validate() {
                return getCurrentRoom() != null;
            }
        });

        validationCriteria.add(new ValidationCriterion("Missing key for room reference.") {
            @Override
            public boolean validate() {
                return getCurrentRoom().isIdentifiable();
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
