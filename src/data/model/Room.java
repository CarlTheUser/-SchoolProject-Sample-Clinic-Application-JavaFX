package data.model;

public class Room {
    int id;
    byte type;
    double hourlyRate;
    int bedCount;
    String locationDetails;

    public Room(byte type, double hourlyRate, int bedCount, String locationDetails) {
        this(0, type, hourlyRate, bedCount, locationDetails);
    }

    public Room(int id, byte type, double hourlyRate, int bedCount, String locationDetails) {
        this.id = id;
        this.type = type;
        this.hourlyRate = hourlyRate;
        this.bedCount = bedCount;
        this.locationDetails = locationDetails;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public int getBedCount() {
        return bedCount;
    }

    public void setBedCount(int bedCount) {
        this.bedCount = bedCount;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }
}
