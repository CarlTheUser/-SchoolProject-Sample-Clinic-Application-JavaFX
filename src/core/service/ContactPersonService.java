package core.service;

import core.model.ContactPerson;
import core.model.Patient;
import data.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class ContactPersonService {
    ErrorListener errorListener;

    public ErrorListener getErrorListener() {
        return errorListener;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public AbstractList<ContactPerson> getPatientContacts(Patient patient){
        final List<ContactPerson> contactPeople = new ArrayList<>();

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("Select Id, Name, ContactNumber, Address, Relation from ContactPerson where PatientId = ?");
            statement.setInt(1, patient.getId());

            resultSet = statement.executeQuery();

            while (resultSet.next()){

                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String contactNumber = resultSet.getString(3);
                String address = resultSet.getString(4);
                String relation = resultSet.getString(5);

                ContactPerson c = ContactPerson.FromDb(id, name, contactNumber, address, relation);
                c.setPatient(patient);
                contactPeople.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if(errorListener != null) errorListener.onError(e.getMessage());
        }finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }


        return new AbstractList<ContactPerson>() {
            @Override
            public ContactPerson get(int index) {
                return contactPeople.get(index);
            }

            @Override
            public int size() {
                return contactPeople.size();
            }
        };
    }
}
