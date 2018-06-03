package core.model;

public enum RoomType {
    Unknown(0), Cabin(1), Emergency(2), ICU(3), Ward(4), Isolation(5), Nursery(6);

    private final int value;

    RoomType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RoomType fromInt(int type){
        switch (type){
            case 1: return Cabin;
            case 2: return Emergency;
            case 3: return ICU;
            case 4: return Ward;
            case 5: return Isolation;
            case 6: return Nursery;
        }
        return Unknown;
    }
}
