package core.model;

public enum Gender {
    None(0), Male(1), Female(2);

    private final int value;

    Gender(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Gender fromInt(int gender){
        switch (gender){
            case 1: return Male;
            case 2: return Female;
        }
        return None;
    }
}
