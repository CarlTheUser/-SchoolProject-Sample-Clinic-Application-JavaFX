package core.service;

import core.model.AdmissionRecord;
import core.model.Patient;
import core.model.Room;
import core.model.VisitRecord;
import data.Database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class AdmissionService {

    public AbstractList<AdmissionRecord> getCurrentAdmissions(){
        List<AdmissionRecord> admissions = new ArrayList<>();

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select DISTINCT a.VisitId as AdmissionId, p.Id as PatientId, p.Firstname, p.Middlename, p.Lastname, a.InitialFindings, a.AdmissionDate, r.Id as RoomNumber, r.LocationDetails \n" +
                    "From Admission a \n" +
                    "Join Visit v \n" +
                    "On v.Id = a.VisitId \n" +
                    "Join Patient p \n" +
                    "On v.PatientId = p.Id \n" +
                    "Join Service s \n" +
                    "On s.VisitId  = v.Id \n" +
                    "Join RoomService rs \n" +
                    "On s.ServiceId = rs.Id \n" +
                    "Join Room r \n" +
                    "On rs.RoomId = r.id \n" +
                    "Where a.DischargeDate is null\n" +
                    "GROUP by a.VisitId");

            resultSet = statement.executeQuery();

            while (resultSet.next()){
                int admissionId = resultSet.getInt(1);
                int patientId = resultSet.getInt(2);
                String firstname = resultSet.getString(3);
                String middlename = resultSet.getString(4);
                String lastname = resultSet.getString(5);
                String initialFindings = resultSet.getString(6);
                LocalDateTime admissionDate = resultSet.getTimestamp(7).toLocalDateTime();
                int roomNumber = resultSet.getInt(8);
                String locationDetails = resultSet.getString(9);
                Patient p = Patient.FromDbPartial(patientId, firstname, middlename, lastname);
                Room r = Room.FromDbPartial(roomNumber, locationDetails);
                VisitRecord v = VisitRecord.FromDbPartial(admissionId, p);
                AdmissionRecord a = AdmissionRecord.FromDbPartial(v, r, initialFindings, admissionDate);
                admissions.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

        return new AbstractList<AdmissionRecord>() {
            @Override
            public AdmissionRecord get(int index) {
                return admissions.get(index);
            }

            @Override
            public int size() {
                return admissions.size();
            }
        };
    }

    public AdmissionRecord getVisitAdmission(VisitRecord visitRecord){
        Connection connection = Database.createConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select InitialFindings, FinalFindings, AdmissionDate, DischargeDate From Admission Where VisitId = ?");
            statement.setInt(1, visitRecord.getId());
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                String initialFindings = resultSet.getString(1);
                String finalFindings = resultSet.getString(2);
                LocalDateTime admissionDate = resultSet.getTimestamp(3).toLocalDateTime();
                Timestamp timestamp = resultSet.getTimestamp(4);
                LocalDateTime dischargeDate = timestamp != null ? timestamp.toLocalDateTime() : null;
                AdmissionRecord a = AdmissionRecord.FromDb(visitRecord, initialFindings, finalFindings, admissionDate, dischargeDate);
                return a;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }
        return null;
    }

}
