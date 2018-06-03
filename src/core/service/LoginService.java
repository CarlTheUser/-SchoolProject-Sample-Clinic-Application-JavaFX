package core.service;

import core.model.Account;
import core.model.AccountType;
import data.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginService {

    Listener listener;

    ErrorListener errorListener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    private List<Account> accounts;

    public LoginService() {
        accounts = new ArrayList<>();

        Account receptionist = Account.NewInstance(null, null, null);
        receptionist.setId(1);
        receptionist.setUsername("receptionist");
        receptionist.setAccountType(AccountType.Receptionist);
        receptionist.setActive(true);
        accounts.add(receptionist);

        Account doctor = Account.NewInstance(null, null, null);
        doctor.setId(1);
        doctor.setUsername("doctor1");
        doctor.setAccountType(AccountType.Doctor);
        doctor.setActive(true);
        accounts.add(doctor);

        Account admin = Account.NewInstance(null, null, null);
        admin.setId(1);
        admin.setUsername("admin");
        admin.setAccountType(AccountType.Administrator);
        admin.setActive(true);
        accounts.add(admin);
    }

    public void login(String accountIdentifier, String password){
//        for(Account account : accounts){
//            if(accountIdentifier.equals(account.getUsername())){
//                onLoginSuccess(account);
//                return;
//            }
//        }
//        onLoginFailed("Account not found");

        Connection connection = Database.createConnection();
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("SELECT Id, Username, Password, AccountType, IsActive FROM Account WHERE Username = ?");
            statement.setString(1, accountIdentifier);
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                if(password.equals(resultSet.getString(3))){
                    if(resultSet.getBoolean(5)) {
                        Account account = Account.FromDb(
                                resultSet.getInt(1),
                                resultSet.getString(2),
                                AccountType.fromInt(resultSet.getInt(4)),
                                true);
                        onLoginSuccess(account);
                    } else onLoginFailed("Account is deactivated.");
                } else onLoginFailed("Wrong password for " + accountIdentifier);
            } else onLoginFailed("Account not found");

        } catch (SQLException e) {
            e.printStackTrace();
            if(errorListener != null) errorListener.onError(e.getMessage());
        } finally {
            Database.closeDatabaseResource(connection, statement, resultSet);
        }

    }

    private void onLoginSuccess(Account account){
        if(listener != null) listener.onLoginSuccees(account);
    }

    private void onLoginFailed(String message){
        if(listener != null) listener.onLoginFailed(message);
    }

    public interface Listener extends ErrorListener{
        void onLoginSuccees(Account account);
        void onLoginFailed(String message);
    }
}
