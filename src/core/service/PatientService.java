package core.service;

import core.model.Gender;
import core.model.Patient;
import core.model.StaffInformation;
import core.model.VisitRecord;
import core.model.readonly.PatientVisit;
import data.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class PatientService {

    private static final String PATIENT_BASE_QUERY = "Select p.Id, p.Firstname, p.Middlename, p.Lastname, p.Gender, p.Birthdate, p.ContactNumber, p.Address, p.Nationality, p.Religion From Patient p";

    public AbstractList<Patient> getAllPatients(){
        List<Patient> patients = new ArrayList<>();

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(PATIENT_BASE_QUERY);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){

                int id = resultSet.getInt(1);
                String firstname = resultSet.getString(2);
                String middlename = resultSet.getString(3);
                String lastname = resultSet.getString(4);
                int gender = resultSet.getInt(5);
                LocalDate birthdate = resultSet.getDate(6).toLocalDate();
                String contact = resultSet.getString(7);
                String address = resultSet.getString(8);
                String nationality = resultSet.getString(9);
                String religion = resultSet.getString(10);

                Patient p = Patient.FromDb(id, firstname, middlename, lastname, Gender.fromInt(gender), birthdate, contact, address, nationality, religion);
                patients.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement);
        }

        return new AbstractList<Patient>() {
            @Override
            public Patient get(int index) {
                return patients.get(index);
            }

            @Override
            public int size() {
                return patients.size();
            }
        };
    }

    public AbstractList<Patient> getAllPatientsPartial(){
        List<Patient> patients = new ArrayList<>();
        Connection connection = Database.createConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("Select p.Id, p.Firstname, p.Middlename, p.Lastname From Patient p");
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                String firstname = resultSet.getString(2);
                String middlename = resultSet.getString(3);
                String lastname = resultSet.getString(4);
                patients.add(Patient.FromDbPartial(id, firstname, middlename, lastname));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }
        return new AbstractList<Patient>() {
            @Override
            public Patient get(int index) {
                return patients.get(index);
            }

            @Override
            public int size() {
                return patients.size();
            }
        };
    }

    public Patient getPatientById(int id){

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(PATIENT_BASE_QUERY + " WHERE Id = ?");
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()){
                int patientId = resultSet.getInt(1);
                String firstname = resultSet.getString(2);
                String middlename = resultSet.getString(3);
                String lastname = resultSet.getString(4);
                int gender = resultSet.getInt(5);
                LocalDate birthdate = resultSet.getDate(6).toLocalDate();
                String contact = resultSet.getString(7);
                String address = resultSet.getString(8);
                String nationality = resultSet.getString(9);
                String religion = resultSet.getString(10);

                Patient p = Patient.FromDb(id, firstname, middlename, lastname, Gender.fromInt(gender), birthdate, contact, address, nationality, religion);
                return p;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement);
        }

        return null;
    }

    public Patient getVisitingPatient(VisitRecord visitRecord){
        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(PATIENT_BASE_QUERY + " Join Visit v on v.PatientId = p.Id Where v.Id = ?");
            statement.setInt(1, visitRecord.getId());
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                int patientId = resultSet.getInt(1);
                String firstname = resultSet.getString(2);
                String middlename = resultSet.getString(3);
                String lastname = resultSet.getString(4);
                int gender = resultSet.getInt(5);
                LocalDate birthdate = resultSet.getDate(6).toLocalDate();
                String contact = resultSet.getString(7);
                String address = resultSet.getString(8);
                String nationality = resultSet.getString(9);
                String religion = resultSet.getString(10);

                Patient p = Patient.FromDb(patientId, firstname, middlename, lastname, Gender.fromInt(gender), birthdate, contact, address, nationality, religion);
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

        return null;
    }

    public AbstractList<Patient> getDoctorPatients(StaffInformation doctor){
        List<Patient> patients = new ArrayList<>();

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select Distinct p.Id, p.Firstname, p.Middlename, p.Lastname, p.Gender, p.Birthdate, p.ContactNumber, p.Address, p.Nationality, p.Religion " +
                    "From Patient p " +
                    "Join Visit v " +
                    "On v.PatientId = p.Id " +
                    "Join Service s " +
                    "On s.VisitId = v.Id " +
                    "Join ConsultationService cs " +
                    "On s.ServiceId = cs.Id " +
                    "Join Staff st " +
                    "On cs.DoctorId = st.Id " +
                    "Where st.Id = ?");

            statement.setInt(1, doctor.getId());

            resultSet = statement.executeQuery();

            while (resultSet.next()){
                int patientId = resultSet.getInt(1);
                String firstname = resultSet.getString(2);
                String middlename = resultSet.getString(3);
                String lastname = resultSet.getString(4);
                int gender = resultSet.getInt(5);
                LocalDate birthdate = resultSet.getDate(6).toLocalDate();
                String contact = resultSet.getString(7);
                String address = resultSet.getString(8);
                String nationality = resultSet.getString(9);
                String religion = resultSet.getString(10);

                Patient p = Patient.FromDb(patientId, firstname, middlename, lastname, Gender.fromInt(gender), birthdate, contact, address, nationality, religion);
                patients.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

        return new AbstractList<Patient>() {
            @Override
            public Patient get(int index) {
                return patients.get(index);
            }

            @Override
            public int size() {
                return patients.size();
            }
        };
    }

    public AbstractList<PatientVisit> getConsultingPatients(StaffInformation doctor){
        List<PatientVisit> patientVisits = new ArrayList<>();

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select p.Id, p.Firstname, p.Middlename, p.Lastname, MAX(s.ServiceDate)" +
                    "From ConsultationService cs " +
                    "Join Service s " +
                    "On s.ServiceId = cs.Id " +
                    "Join Visit v " +
                    "On s.VisitId = v.Id " +
                    "Join Patient p " +
                    "On v.PatientId = p.Id " +
                    "Where cs.DoctorId = ? " +
                    "GROUP by p.Id " +
                    "Order By cs.Id Desc");
            statement.setInt(1, doctor.getId());
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                int patientId = resultSet.getInt(1);
                String firstname = resultSet.getString(2);
                String middlename = resultSet.getString(3);
                String lastname = resultSet.getString(4);
                LocalDateTime visitDate = resultSet.getTimestamp(5).toLocalDateTime();
                Patient p = Patient.FromDbPartial(patientId, firstname, middlename, lastname);
                PatientVisit pv = PatientVisit.FromDb(p, visitDate);
                patientVisits.add(pv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

        return new AbstractList<PatientVisit>() {
            @Override
            public PatientVisit get(int index) {
                return patientVisits.get(index);
            }

            @Override
            public int size() {
                return patientVisits.size();
            }
        };
    }

}