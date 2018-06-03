package data.model;

import java.time.LocalDate;

public class Vitals {
    int patientId;
    String bloodPressure;
    String respiratoryRate;
    String weight;
    String height;
    String temperature;
    LocalDate taken;

    public Vitals(int patientId, String bloodPressure, String respiratoryRate, String weight, String height, String temperature, LocalDate taken) {
        this.patientId = patientId;
        this.bloodPressure = bloodPressure;
        this.respiratoryRate = respiratoryRate;
        this.weight = weight;
        this.height = height;
        this.temperature = temperature;
        this.taken = taken;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(String respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public LocalDate getTaken() {
        return taken;
    }

    public void setTaken(LocalDate taken) {
        this.taken = taken;
    }
}
