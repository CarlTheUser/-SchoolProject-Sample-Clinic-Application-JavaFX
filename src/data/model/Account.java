package data.model;

public class Account {
    int id;
    String username;
    String password;
    byte accountType;
    boolean isActive;

    public Account(){}

    public Account(String username, String password, byte accountType, boolean isActive) {
        this.username = username;
        this.password = password;
        this.accountType = accountType;
        this.isActive = isActive;
    }

    public Account(int id, String username, String password, byte accountType, boolean isActive) {
        this(username, password, accountType, isActive);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte getAccountType() {
        return accountType;
    }

    public void setAccountType(byte accountType) {
        this.accountType = accountType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
