package core.model;

public enum Position {
    None(0), Receptionist(1), Cashier(2), Laboratory(3), Doctor(4);

    private final int value;

    Position(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
