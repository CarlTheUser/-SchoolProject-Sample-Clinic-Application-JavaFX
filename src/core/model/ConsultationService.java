package core.model;

import data.Database;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class ConsultationService extends Service {

    private final SimpleObjectProperty<StaffInformation> doctor = new SimpleObjectProperty<>(this, "doctor", null);
    private final SimpleStringProperty symptoms = new SimpleStringProperty(this, "symptoms", "");
    private final SimpleStringProperty diagnosis = new SimpleStringProperty(this, "diagnosis", "");
    private final SimpleStringProperty doctorNotes = new SimpleStringProperty(this, "doctorNotes", "");

    public static ConsultationService NewInstance(VisitRecord visitRecord, StaffInformation doctor, SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener){
        ConsultationService c = new ConsultationService(visitRecord, doctor, LocalDateTime.now(), saveListener, validationListener, errorListener);
        c.setDescription("Patient Consultation");
        c.isNew = true;
        return c;
    }

    public static ConsultationService FromDbPartial(int id, VisitRecord visitRecord, String description, int serviceId, double fee, LocalDateTime serviceDate){
        ConsultationService c = new ConsultationService(visitRecord, null, serviceDate, null, null, null);
        c.setId(id);
        c.setDescription(description);
        c.setServiceId(serviceId);
        c.setFee(fee);
        c.isNew = false;
        return c;
    }

    public static ConsultationService FromDbPartial(int id, StaffInformation doctor, LocalDateTime serviceDate, String symptoms, String diagnosis){
        ConsultationService c = new ConsultationService(null, doctor, serviceDate, null, null, null);
        c.isNew = false;
        return c;
    }


    private ConsultationService(VisitRecord visitRecord, StaffInformation doctor, LocalDateTime serviceDate, SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener) {
        super(visitRecord, serviceDate, saveListener, validationListener, errorListener);
        setDoctor(doctor);
    }




    @Override
    public ServiceType getType() {
        return ServiceType.ConsultationService;
    }

    @Override
    public double calculateFee() {
        return getFee();
    }

    @Override
    public ValidationCriterion isPayable() {
        return null;
    }

    public StaffInformation getDoctor() {
        return doctor.get();
    }

    public SimpleObjectProperty<StaffInformation> doctorProperty() {
        return doctor;
    }

    public void setDoctor(StaffInformation doctor) {
        this.doctor.set(doctor);
    }

    public String getSymptoms() {
        return symptoms.get();
    }

    public SimpleStringProperty symptomsProperty() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms.set(symptoms);
    }

    public String getDiagnosis() {
        return diagnosis.get();
    }

    public SimpleStringProperty diagnosisProperty() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis.set(diagnosis);
    }

    public String getDoctorNotes() {
        return doctorNotes.get();
    }

    public SimpleStringProperty doctorNotesProperty() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes.set(doctorNotes);
    }

    @Override
    protected void saveMethod() throws SQLException {

        Connection connection = Database.createConnection();

        PreparedStatement consultationStatement = null;

        PreparedStatement serviceStatement = null;

        ResultSet resultSet = null;

        try {
            connection.setAutoCommit(false);
            consultationStatement = connection.prepareStatement("Insert into ConsultationService(DoctorId, Symptoms, Diagnosis, Notes) Values(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            consultationStatement.setInt(1, getDoctor().getId());
            consultationStatement.setString(2, getSymptoms());
            consultationStatement.setString(3, getDiagnosis());
            consultationStatement.setString(4, getDoctorNotes());
            consultationStatement.executeUpdate();
            resultSet = consultationStatement.getGeneratedKeys();
            if(resultSet.next()){
                int id = resultSet.getInt(1);
                setServiceId(id);
            }
            resultSet.close();

            serviceStatement = connection.prepareStatement("Insert Into Service(VisitId, ServiceType, Description, ServiceId, Fee, ServiceDate) Values(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            serviceStatement.setInt(1, getVisitRecord().getId());
            serviceStatement.setInt(2, getType().getValue());
            serviceStatement.setString(3, getDescription());
            serviceStatement.setInt(4, getServiceId());
            serviceStatement.setDouble(5, getFee());
            serviceStatement.setTimestamp(6, Timestamp.valueOf(getServieDate()));
            serviceStatement.executeUpdate();
            resultSet = serviceStatement.getGeneratedKeys();
            if(resultSet.next()){
                int id = resultSet.getInt(1);
                setId(id);
            }


            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
            throw e;
        }finally {
            connection.setAutoCommit(true);
            Database.closeDatabaseResource(connection, consultationStatement, serviceStatement, resultSet);
        }
    }

    @Override
    protected void updateMethod() {

    }

    @Override
    protected void deleteMethod() {

    }

    @Override
    protected void refreshMethod() {

    }

    @Override
    protected AbstractList<ValidationCriterion> createValidationCriteria() {
        AbstractList<ValidationCriterion> superCriteria = super.createValidationCriteria();
        List<ValidationCriterion> validationCriteria = new ArrayList<>();
        if(superCriteria != null) validationCriteria.addAll(superCriteria);

        validationCriteria.add(new ValidationCriterion("Reference to doctor not found.") {
            @Override
            public boolean validate() {
                return getDoctor() != null && getDoctor().isIdentifiable();
            }
        });

        validationCriteria.add(new ValidationCriterion("Patient symptoms required.") {
            @Override
            public boolean validate() {
                return getSymptoms().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Diagnosis for patient is required.") {
            @Override
            public boolean validate() {
                return getDiagnosis().trim().length() > 0;
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
