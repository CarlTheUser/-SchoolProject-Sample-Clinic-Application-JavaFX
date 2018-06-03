package core.model;

import core.service.StaffService;
import data.Database;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Account extends Model{

    final SimpleIntegerProperty id = new SimpleIntegerProperty(this, "id", 0);
    final SimpleStringProperty username = new SimpleStringProperty(this,"username", "");
    final SimpleObjectProperty<AccountType> accountType = new SimpleObjectProperty<>(this, "accountType", AccountType.None);
    final SimpleBooleanProperty active = new SimpleBooleanProperty(this, "active", true);
    final SimpleObjectProperty<StaffInformation> staffInformation = new SimpleObjectProperty<>(this, "staffInformation", null);

    private String password = null;

    public static Account NewInstance(SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener){
        Account a = new Account();
        a.setSaveListener(saveListener);
        a.setValidationListener(validationListener);
        a.setErrorListener(errorListener);
//        a.setDirty(true);
        a.isNew = true;
        return a;
    }

    public static Account FromDb(int id, String username, AccountType accountType, boolean active){
        Account a = new Account(id, username, accountType, active);
        a.isNew = false;
        return a;
    }

    private Account(int id, String username, AccountType accountType, boolean active){
        setId(id);
        setUsername(username);
        setAccountType(accountType);
        setActive(active);
    }

    Account(){
        setRestoreBackupOnInvalid(false);
    }

    public void activate(){
        beginEdit();
        setActive(true);
        applyEdit();
    }

    public void deactivate(){
        beginEdit();
        setActive(false);
        applyEdit();
    }

    public void changePassword(String newPassword){
        Connection connection = Database.createConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("Update Account Set Password = ? Where Id = ?");
            statement.setString(1, newPassword);
            statement.setInt(2, getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeDatabaseResource(connection);
        }
    }

    private boolean staffInformationLoaded = false;

    private void loadStaffInformation(){
        if(isIdentifiable()){
            StaffService staffService = new StaffService();
            StaffInformation staffInformation = staffService.getStaffByAccount(this);
            setStaffInformation(staffInformation);
        }
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public AccountType getAccountType() {
        return accountType.get();
    }

    public SimpleObjectProperty<AccountType> accountTypeProperty() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType.set(accountType);
    }

    public boolean isActive() {
        return active.get();
    }

    public SimpleBooleanProperty activeProperty() {
        return active;
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    public StaffInformation getStaffInformation() {
        if(!staffInformationLoaded){
            loadStaffInformation();
            staffInformationLoaded = true;
        }
        return staffInformation.get();
    }

    public SimpleObjectProperty<StaffInformation> staffInformationProperty() {
        return staffInformation;
    }

    public void setStaffInformation(StaffInformation staffInformation) {
        if(staffInformation == null) return;
        this.staffInformation.set(staffInformation);
        staffInformationLoaded = true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String usernameBackup = null;
    private AccountType accountTypeBackup = null;
    private Boolean activeBackup = null;

    @Override
    protected void backupFields() {
        usernameBackup = getUsername();
        accountTypeBackup = getAccountType();
        activeBackup = isActive();
    }

    @Override
    protected void restoreFields() {
        setUsername(usernameBackup);
        setAccountType(accountTypeBackup);
        setActive(activeBackup);
    }

    @Override
    protected void clearBackup() {
        usernameBackup = null;
        accountTypeBackup = null;
        activeBackup = null;
    }

    @Override
    protected void saveMethod() throws Exception{
        Connection connection = Database.createConnection();
        PreparedStatement accountStatement = null;
        PreparedStatement staffStatement = null;
        final StaffInformation staff = getStaffInformation();
        try {
            connection.setAutoCommit(false);
            accountStatement = connection.prepareStatement("Insert into Account values(?, ?, ?, ?, ?)");
            accountStatement.setInt(1, getId());
            accountStatement.setString(2, getUsername());
            accountStatement.setString(3, password);
            accountStatement.setInt(4, getAccountType().getValue());
            accountStatement.setBoolean(5, true);
            accountStatement.executeUpdate();
            staffStatement = connection.prepareStatement("Insert into Staff values(?, ?, ?,  ?, ?, ?, ?, ?, ?, ?, ?)");
            staffStatement.setInt(1, staff.getId());
            staffStatement.setString(2, staff.getFirstname());
            staffStatement.setString(3, staff.getMiddlename());
            staffStatement.setString(4, staff.getLastname());
            staffStatement.setInt(5, staff.getGender().getValue());
            staffStatement.setDate(6, Date.valueOf(staff.getBirthDate()));
            staffStatement.setString(7, staff.getPosition());
            staffStatement.setString(8, staff.getExpertise());
            staffStatement.setString(9, staff.getContactNumber());
            staffStatement.setString(10, staff.getEmail());
            staffStatement.setString(11, staff.getAddress());
            staffStatement.executeUpdate();
            connection.commit();
            staff.onBatchSaved();
            isNew = false;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            Database.closeDatabaseResource(connection, accountStatement);
        }
    }

    @Override
    protected void updateMethod() throws SQLException {
        Connection connection = Database.createConnection();
        PreparedStatement statement = connection.prepareStatement("Update Account Set Username = ?, AccountType = ?, IsActive = ? Where Id = ?");
        statement.setString(1, getUsername());
        statement.setInt(2, getAccountType().getValue());
        statement.setBoolean(3, isActive());
        statement.setInt(4, getId());
        statement.executeUpdate();
        Database.closeDatabaseResource(connection, statement);
    }

    @Override
    protected void deleteMethod() {

    }

    @Override
    protected void refreshMethod() {

    }

    @Override
    protected boolean isIdentifiable() {
        return getId() > 0;
    }

    @Override
    protected AbstractList<ValidationCriterion> createValidationCriteria() {
        List<ValidationCriterion> validationCriteria = new ArrayList<>();
        validationCriteria.add(new ValidationCriterion("Username should not be blank.") {
            @Override
            public boolean validate() {
                return getUsername().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Account type is not initialized.") {
            @Override
            public boolean validate() {
                return getAccountType() != AccountType.None;
            }
        });

        if(isNew){
            validationCriteria.add(new ValidationCriterion("Staff account holder information nt set.") {
                @Override
                public boolean validate() {
                    return getStaffInformation() != null;
                }
            });

            validationCriteria.add(new ValidationCriterion("Staff account holder information have errors.") {
                @Override
                public boolean validate() {
                    return getStaffInformation().isValid();
                }
            });
        }

        return new AbstractList<ValidationCriterion>() {
            @Override
            public ValidationCriterion get(int index) {
                return validationCriteria.get(index);
            }

            @Override
            public int size() {
                return validationCriteria.size();
            }
        };
    }
}
