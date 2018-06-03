package core.service;

import core.model.Patient;
import data.Database;
import core.model.Vitals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class VitalsService {
    private static final String VITALS_BASE_QUERY = "Select PatientId, BloodPressure, RespiratoryRate, Weight, Height, Temperature, DateTaken From Vitals";

    public Vitals getPatientVitals(Patient patient){
        Connection connection = Database.createConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(VITALS_BASE_QUERY + " Where PatientId = ?");
            statement.setInt(1, patient.getId());
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                String bloodPressure = resultSet.getString(2);
                String respiratoryRate = resultSet.getString(3);
                String weight = resultSet.getString(4);
                String height = resultSet.getString(5);
                String temperature = resultSet.getString(6);
                LocalDateTime dateTaken = resultSet.getTimestamp(7).toLocalDateTime();
                Vitals v = Vitals.FromDb(patient.getId(), bloodPressure, respiratoryRate, weight, height, temperature, dateTaken);
                return v;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
