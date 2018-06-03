package core.service;

import core.model.Patient;
import core.model.Service;
import core.model.ServiceType;
import core.model.VisitRecord;
import data.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class VisitRecordService {

    private static final String VISITRECORD_BASE_QUERY = "Select v.Id, v.PatientId, p.Firstname, p.Middlename, p.Lastname, v.Notes, v.VisitDate From Visit v Join Patient p On v.PatientId = p.Id";

    public AbstractList<VisitRecord> getVisitRecordsByPatient(Patient patient){

        List<VisitRecord> visitRecords = new ArrayList<>();

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(VISITRECORD_BASE_QUERY + " Where p.Id = ?");
            statement.setInt(1, patient.getId());

            resultSet = statement.executeQuery();

            while (resultSet.next()){

                int id = resultSet.getInt(1);
                String notes = resultSet.getString(6);
                LocalDateTime visitDate = resultSet.getTimestamp(7).toLocalDateTime();

                VisitRecord v = VisitRecord.FromDb(id, notes, visitDate);
                v.setPatient(patient);
                visitRecords.add(v);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

        return new AbstractList<VisitRecord>() {
            @Override
            public VisitRecord get(int index) {
                return visitRecords.get(index);
            }

            @Override
            public int size() {
                return visitRecords.size();
            }
        };
    }

    public AbstractList<VisitRecord> getRecentVisits(){
        List<VisitRecord> visitRecords = new ArrayList<>();
        Connection connection = Database.createConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(VISITRECORD_BASE_QUERY + " Where v.VisitDate >= now() - INTERVAL 1 DAY AND (Select s.Id From Service s Where VisitId = v.Id And ServiceType = ?) is null");
            statement.setInt(1, ServiceType.ConsultationService.getValue());
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                int patientId = resultSet.getInt(2);
                String firstname = resultSet.getString(3);
                String middlename = resultSet.getString(4);
                String lastname = resultSet.getString(5);
                String notes = resultSet.getString(6);
                LocalDateTime visitDate = resultSet.getTimestamp(7).toLocalDateTime();
                VisitRecord v = VisitRecord.FromDb(id, notes, visitDate);
                Patient p = Patient.FromDbPartial(patientId, firstname, middlename, lastname);
                v.setPatient(p);
                visitRecords.add(v);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

        return new AbstractList<VisitRecord>() {
            @Override
            public VisitRecord get(int index) {
                return visitRecords.get(index);
            }

            @Override
            public int size() {
                return visitRecords.size();
            }
        };
    }

    public VisitRecord getVisitRecordById(int id){
        Connection connection = Database.createConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(VISITRECORD_BASE_QUERY + " Where v.Id = ?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                int patientId = resultSet.getInt(2);
                String firstname = resultSet.getString(3);
                String middlename = resultSet.getString(4);
                String lastname = resultSet.getString(5);
                String notes = resultSet.getString(6);
                LocalDateTime visitDate = resultSet.getTimestamp(7).toLocalDateTime();
                VisitRecord v = VisitRecord.FromDb(id, notes, visitDate);
                Patient p = Patient.FromDbPartial(patientId, firstname, middlename, lastname);
                v.setPatient(p);
                return v;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }
        return null;
    }

    public VisitRecord getVisitRecordByService(Service service){

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(VISITRECORD_BASE_QUERY + " Join Service s on v.Id = s.VisitId Where s.Id = ?");
            statement.setInt(1, service.getId());
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                int visitId = resultSet.getInt(1);
                int patientId = resultSet.getInt(2);
                String firstname = resultSet.getString(3);
                String middlename = resultSet.getString(4);
                String lastname = resultSet.getString(5);
                String notes = resultSet.getString(6);
                LocalDateTime visitDate = resultSet.getTimestamp(7).toLocalDateTime();
                VisitRecord v = VisitRecord.FromDb(visitId, notes, visitDate);
                Patient p = Patient.FromDbPartial(patientId, firstname, middlename, lastname);
                v.setPatient(p);
                return v;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }
        return null;
    }
}
