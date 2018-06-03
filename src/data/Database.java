package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;

public class Database {
    public static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/db_apl?useSSL=false";

    public static Connection createConnection(){
        try {
            Connection connection = DriverManager.getConnection(CONNECTION_STRING, "root", "");
            connection.setAutoCommit(true);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void closeDatabaseResource(AutoCloseable... closeables){
        try {
            for (AutoCloseable closeable : closeables) {
                if(closeable != null) closeable.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

//    public static AbstractList Query(String query, ResultSetMapper mapper){
//        Connection connection = createConnection();
//
//
//
//    }

    public interface ResultSetMapper<T>{
        AbstractList<T> MapResultSet(ResultSet resultSet);
    }

}
