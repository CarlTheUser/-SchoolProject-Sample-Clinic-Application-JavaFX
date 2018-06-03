package core.model;

public enum AccountType {
    None(0), Administrator(1), Receptionist(2), Doctor(3), Laboratory(4), Cashier(5);

    private final int value;

    AccountType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AccountType fromInt(int accountType){
        switch (accountType){
            case 1: return Administrator;
            case 2: return Receptionist;
            case 3: return Doctor;
            case 4: return Laboratory;
            case 5: return Cashier;
        }
        return None;
    }

}
