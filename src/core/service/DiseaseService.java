package core.service;

import core.model.Disease;
import core.model.Patient;
import data.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class DiseaseService {
    ErrorListener errorListener;

    public ErrorListener getErrorListener() {
        return errorListener;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public AbstractList<Disease> getPatientConditions(Patient patient){

        List<Disease> conditions = new ArrayList<>();

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select Id, Name, Description, Status from PatientCondition where PatientId = ?");
            statement.setInt(1, patient.getId());

            resultSet = statement.executeQuery();

            while (resultSet.next()){

                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String description = resultSet.getString(3);
                String status = resultSet.getString(4);
                Disease d = Disease.FromDb(id, name, description, status);
                d.setPatient(patient);
                conditions.add(d);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if(errorListener != null) errorListener.onError(e.getMessage());
        }finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

        return new AbstractList<Disease>() {
            @Override
            public Disease get(int index) {
                return conditions.get(index);
            }

            @Override
            public int size() {
                return conditions.size();
            }
        };
    }
}
