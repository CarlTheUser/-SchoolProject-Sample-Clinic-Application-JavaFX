package core.model;

public enum ServiceType {
    Unknown(0), RoomService(1), OperationService(2), LaboratoryService(3), ConsultationService(4), MedicineAndEquipmentService(5);

    private final int value;

    ServiceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static  ServiceType fromInt(int value){
        switch (value){
            case 1: return RoomService;
            case 2: return OperationService;
            case 3: return LaboratoryService;
            case 4: return ConsultationService;
            case 5: return MedicineAndEquipmentService;
        }
        return Unknown;
    }

}
