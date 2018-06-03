package core.service;

import core.model.Account;
import core.model.AccountType;
import core.model.StaffInformation;
import data.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountService {

    private static final String BASE_ACCOUNT_QUERY = "Select Id, Username, AccountType, IsActive From Account Where Id = ?";

    public Account getStaffAccount(StaffInformation staff){

        Connection connection = Database.createConnection();

        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(BASE_ACCOUNT_QUERY);
            statement.setInt(1, staff.getId());
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                int id = resultSet.getInt(1);
                String username = resultSet.getString(2);
                int accountType = resultSet.getInt(3);
                boolean isActive = resultSet.getBoolean(4);

                Account a = Account.FromDb(id, username, AccountType.fromInt(accountType), isActive);
                return a;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
