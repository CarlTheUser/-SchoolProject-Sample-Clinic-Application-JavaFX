package core.service;

import core.model.Account;
import core.model.Gender;
import core.model.Patient;
import core.model.StaffInformation;
import core.model.readonly.DoctorPatientVisit;
import data.Database;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class StaffService {

    ErrorListener errorListener;

    public ErrorListener getErrorListener() {
        return errorListener;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public StaffInformation getStaffByAccount(Account account){

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select Firstname, Middlename, Lastname, Gender, Birthdate, Position, Expertise, ContactNumber, Email, Address FROM Staff WHERE Id = ?");

            statement.setInt(1, account.getId());

            resultSet = statement.executeQuery();

            if(resultSet.next()){
                String firstname = resultSet.getString(1);
                String middlename = resultSet.getString(2);
                String lastname = resultSet.getString(3);
                int gender = resultSet.getInt(4);
                LocalDate birthdate = resultSet.getDate(5).toLocalDate();
                String position = resultSet.getString(6);
                String expertise = resultSet.getString(7);
                String contactNumber = resultSet.getString(8);
                String email = resultSet.getString(9);
                String address = resultSet.getString(10);

                StaffInformation s = StaffInformation.FromDb(account.getId(), firstname, middlename, lastname, Gender.fromInt(gender), birthdate, position, expertise, contactNumber, email, address);
                s.setAccount(account);
                return s;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if(errorListener != null) errorListener.onError(e.getMessage());
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

        return null;
    }

    public AbstractList<StaffInformation> getAllStaff(){

        final List<StaffInformation> staffInformations = new ArrayList<>();

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select Id, Firstname, Middlename, Lastname, Gender, Birthdate, Position, Expertise, ContactNumber, Email, Address FROM Staff");

            resultSet = statement.executeQuery();

            while(resultSet.next()){
                int id = resultSet.getInt(1);
                String firstname = resultSet.getString(2);
                String middlename = resultSet.getString(3);
                String lastname = resultSet.getString(4);
                int gender = resultSet.getInt(5);
                LocalDate birthdate = resultSet.getDate(6).toLocalDate();
                String position = resultSet.getString(7);
                String expertise = resultSet.getString(8);
                String contactNumber = resultSet.getString(9);
                String email = resultSet.getString(10);
                String address = resultSet.getString(11);
                StaffInformation s = StaffInformation.FromDb(id, firstname, middlename, lastname, Gender.fromInt(gender), birthdate, position, expertise, contactNumber, email, address);
                staffInformations.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

        return new AbstractList<StaffInformation>() {
            @Override
            public StaffInformation get(int index) {
                return staffInformations.get(index);
            }

            @Override
            public int size() {
                return staffInformations.size();
            }
        };
    }

    public AbstractList<DoctorPatientVisit> getAllDoctorPatient(){
        List<DoctorPatientVisit> doctorPatientVisits = new ArrayList<>();

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select s.Id as StaffId, s.Firstname, s.Middlename, s.Lastname, p.Id, p.Firstname, p.Middlename, p.Lastname, svc.ServiceDate as LastVisit " +
                    "From Staff s " +
                    "Join Account a " +
                    "On a.Id = s.Id " +
                    "left Join ConsultationService cs " +
                    "on cs.DoctorId = s.Id " +
                    "Join Service svc " +
                    "on svc.ServiceId  = cs.Id " +
                    "Join Visit v " +
                    "on v.Id = svc.VisitId " +
                    "join Patient p " +
                    "on p.Id = v.PatientId " +
                    "Where a.AccountType = 3 " +
                    "Order By s.Id");

            resultSet = statement.executeQuery();

            while(resultSet.next()){
                int staffid = resultSet.getInt(1);
                String staffFirstname = resultSet.getString(2);
                String staffMiddlename = resultSet.getString(3);
                String staffLastname = resultSet.getString(4);
                int patientId = resultSet.getInt(5);
                String patientFirstname = resultSet.getString(6);
                String patientMiddlename = resultSet.getString(7);
                String patientLastname = resultSet.getString(8);
                LocalDateTime lastVisit = resultSet.getTimestamp(9).toLocalDateTime();
                StaffInformation s = StaffInformation.FromDbPartial(staffid, staffFirstname, staffMiddlename, staffLastname);
                Patient p = Patient.FromDbPartial(patientId, patientFirstname, patientMiddlename, patientLastname);
                doctorPatientVisits.add(DoctorPatientVisit.FromDb(s, p, lastVisit));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }


        return new AbstractList<DoctorPatientVisit>() {
            @Override
            public DoctorPatientVisit get(int index) {
                return doctorPatientVisits.get(index);
            }

            @Override
            public int size() {
                return doctorPatientVisits.size();
            }
        };
    }
}
