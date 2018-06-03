package core.service;

import core.model.Room;
import core.model.RoomType;
import core.model.VisitRecord;
import core.model.readonly.RoomUsage;
import data.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class RoomService {

    public AbstractList<RoomUsage> getAvailableRooms(){

        List<RoomUsage> roomUsages = new ArrayList<>();

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select r.Id, r.RoomType, r.HourlyRate, r.BedCount, r.LocationDetails, " +
                    "(Select Count(rs.Id) From RoomService rs Where rs.RoomId = r.Id And DateOut is null)  RoomUsage " +
                    "FROM Room r " +
                    "Where (Select Count(rs.Id) From RoomService rs Where rs.RoomId = r.Id And DateOut = null) < r.BedCount;");

            resultSet = statement.executeQuery();

            while (resultSet.next()){
                int id = resultSet.getInt(1);
                int roomType = resultSet.getInt(2);
                double hourlyRate = resultSet.getDouble(3);
                int bedCount = resultSet.getInt(4);
                String locationDetails = resultSet.getString(5);
                int bedUsage = resultSet.getInt(6);
                RoomUsage r = RoomUsage.FromDb(id, RoomType.fromInt(roomType), bedCount, hourlyRate, locationDetails, bedUsage);
                roomUsages.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Database.closeDatabaseResource(connection, statement);
        }

        return new AbstractList<RoomUsage>() {
            @Override
            public RoomUsage get(int index) {
                return roomUsages.get(index);
            }

            @Override
            public int size() {
                return roomUsages.size();
            }
        };
    }

    public AbstractList<Room> getAllRooms(){
        List<Room> rooms = new ArrayList<>();

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select Id, RoomType, HourlyRate, BedCount, LocationDetails from Room");

            resultSet = statement.executeQuery();

            while (resultSet.next()){
                int id = resultSet.getInt(1);
                int roomType = resultSet.getInt(2);
                double hourlyRate = resultSet.getDouble(3);
                int bedCount = resultSet.getInt(4);
                String locationDetails = resultSet.getString(5);

                Room r = Room.FromDb(id, RoomType.fromInt(roomType), bedCount, hourlyRate, locationDetails);
                rooms.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

        return new AbstractList<Room>() {
            @Override
            public Room get(int index) {
                return rooms.get(index);
            }

            @Override
            public int size() {
                return rooms.size();
            }
        };
    }

    public Room getCurrentRoom(VisitRecord visitRecord){
        Connection connection = Database.createConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("Select R.id, r.RoomType, r.HourlyRate, r.BedCount, r.LocationDetails " +
                    "FROM Room r " +
                    "join RoomService rs " +
                    "on r.Id = rs.Id " +
                    "join Service s " +
                    "on rs.Id = s.ServiceId " +
                    "Join Visit v " +
                    "On v.Id = s.VisitId " +
                    "Where v.Id = ? ");

            statement.setInt(1, visitRecord.getId());
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                int id = resultSet.getInt(1);
                int roomType = resultSet.getInt(2);
                double hourlyRate = resultSet.getDouble(3);
                int bedCount = resultSet.getInt(4);
                String locationDetails = resultSet.getString(5);
                Room r = Room.FromDb(id, RoomType.fromInt(roomType), bedCount, hourlyRate, locationDetails);
                return r;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }
        return null;
    }

    public Room getById(int id){
        return null;
    }

    public Room getRoomServiceRoom(core.model.RoomService roomService){
        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select r.id, r.RoomType, r.HourlyRate, r.BedCount, r.LocationDetails From RoomService rs Join Room r On rs.RoomId = r.Id Where rs.Id = ?");
            statement.setInt(1, roomService.getServiceId());
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                int id = resultSet.getInt(1);
                int roomType = resultSet.getInt(2);
                double hourlyRate = resultSet.getDouble(3);
                int bedCount = resultSet.getInt(4);
                String locationDetails = resultSet.getString(5);
                Room r = Room.FromDb(id, RoomType.fromInt(roomType), bedCount, hourlyRate, locationDetails);
                return r;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

        return null;
    }

}
