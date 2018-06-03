package core.service;

import core.model.*;
import core.model.RoomService;
import core.model.readonly.ConsultationData;
import data.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class ServiceService {

    public AbstractList<ConsultationService> getStaffConsultations(StaffInformation staff){

        List<ConsultationService> consultations = new ArrayList<>();

        Connection connection = Database.createConnection();

        ResultSet resultSet = null;

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("Select s.Id, v.Id, s.Description");
            statement.setInt(1, staff.getId());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

        return new AbstractList<ConsultationService>() {
            @Override
            public ConsultationService get(int index) {
                return consultations.get(index);
            }

            @Override
            public int size() {
                return consultations.size();
            }
        };
    }

    public AbstractList<ConsultationData> getPatientConsultationsFromDoctor(Patient patient, StaffInformation doctor){
        List<ConsultationData> consultations = new ArrayList<>();

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select s.Id, s.ServiceDate, cs.Symptoms, cs.Diagnosis, cs.Notes, v.Id as VisitId " +
                    "From ConsultationService cs " +
                    "Join Service s " +
                    "On cs.Id = s.ServiceId " +
                    "Join Staff st " +
                    "On cs.DoctorId = st.Id " +
                    "Join Visit v " +
                    "On s.VisitId = v.Id " +
                    "Join Patient p " +
                    "On p.Id = v.PatientId " +
                    "Where st.Id = ? And p.Id = ?");
            statement.setInt(1, doctor.getId());
            statement.setInt(2, patient.getId());
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                LocalDateTime serviceDate = resultSet.getTimestamp(2).toLocalDateTime();
                String symptoms = resultSet.getString(3);
                String diagnosis = resultSet.getString(4);
                String doctorNotes = resultSet.getString(5);
//                int visitId = resultSet.getInt(5);
//                VisitRecord v = VisitRecord.FromDbPartial(visitId, patient);
//                ConsultationService c = ConsultationService.FromDbPartial(id, doctor, serviceDate, symptoms, diagnosis);
//                c.setVisitRecord(v);
//                consultations.add(c);
                ConsultationData c = ConsultationData.FromDb(id, serviceDate, symptoms, diagnosis, doctorNotes);
                consultations.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

        return new AbstractList<ConsultationData>() {
            @Override
            public ConsultationData get(int index) {
                return consultations.get(index);
            }

            @Override
            public int size() {
                return consultations.size();
            }
        };
    }

    public AbstractList<Service> getPatientUnpaidServices(Patient patient){
        List<Service> services = new ArrayList<>();

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select s.Id, s.VisitId, v.VisitDate, s.ServiceType, s.Description, s.ServiceId, s.Fee, s.ServiceDate " +
                    "From Service s " +
                    "Join Visit v " +
                    "On s.VisitId = v.Id " +
                    "Join Patient p " +
                    "On v.PatientId = p.Id " +
                    "Where p.Id = ? And (s.Payment is null Or s.Payment = 0 Or (s.Payment - s.Fee) < 0)");
            statement.setInt(1, patient.getId());
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                int serviceId = resultSet.getInt(1);
                int visitId = resultSet.getInt(2);
                LocalDateTime visitDate = resultSet.getTimestamp(3).toLocalDateTime();
                ServiceType serviceType = ServiceType.fromInt(resultSet.getInt(4));
                String description = resultSet.getString(5);
                int serviceTypeId = resultSet.getInt(6);
                double fee = resultSet.getDouble(7);
                LocalDateTime serviceDate = resultSet.getTimestamp(8).toLocalDateTime();
                Service service = null;
                VisitRecord v = VisitRecord.FromDbPartial(visitId, patient);
                switch (serviceType){
                    case RoomService: service = RoomService.FromDb(serviceId, v, description, serviceTypeId, fee, serviceDate);
                    break;
                    case ConsultationService: service = ConsultationService.FromDbPartial(serviceId, v, description, serviceTypeId, fee, serviceDate);
                    break;
                    case MedicineAndEquipmentService: service = MedicineAndEquipmentService.FromDb(serviceId, v, description, serviceTypeId, fee, serviceDate);
                    break;
                }
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

        return new AbstractList<Service>() {
            @Override
            public Service get(int index) {
                return services.get(index);
            }

            @Override
            public int size() {
                return services.size();
            }
        };
    }

    public AbstractList<Service> getVisitServices(VisitRecord visitRecord){
        List<Service> services = new ArrayList<>();

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select s.Id, s.ServiceType, s.Description, s.ServiceId, s.Fee, s.ServiceDate " +
                    "From Service s " +
                    "Join Visit v " +
                    "On s.VisitId = v.Id " +
                    "Join Patient p " +
                    "On v.PatientId = p.Id " +
                    "Where v.Id = ?");
            statement.setInt(1, visitRecord.getId());
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                int serviceId = resultSet.getInt(1);
                ServiceType serviceType = ServiceType.fromInt(resultSet.getInt(2));
                String description = resultSet.getString(3);
                int serviceTypeId = resultSet.getInt(4);
                double fee = resultSet.getDouble(5);
                LocalDateTime serviceDate = resultSet.getTimestamp(6).toLocalDateTime();
                Service service = null;
                switch (serviceType){
                    case RoomService: service = RoomService.FromDb(serviceId, visitRecord, description, serviceTypeId, fee, serviceDate);
                        break;
                    case ConsultationService: service = ConsultationService.FromDbPartial(serviceId, visitRecord, description, serviceTypeId, fee, serviceDate);
                        break;
                    case MedicineAndEquipmentService: service = MedicineAndEquipmentService.FromDb(serviceId, visitRecord, description, serviceTypeId, fee, serviceDate);
                        break;
                }
                services.add(service);
            }

        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

        return new AbstractList<Service>() {
            @Override
            public Service get(int index) {
                return services.get(index);
            }

            @Override
            public int size() {
                return services.size();
            }
        };
    }

    public core.service.RoomService getRoomServiceInformationById(int serviceId){
        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {

            statement = connection.prepareStatement("Select rs.Id, rs.RoomId, rs.DateIn, rs.DateOut From RoomService rs join Service s On s.ServiceId = rs.Id Where rs.Id = ?");
            statement.setInt(1, serviceId);
            resultSet = statement.executeQuery();
            while (resultSet.next()){



            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }


        return null;
    }



}
