package core.model.readonly;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class ConsultationData {
    private final ReadOnlyIntegerWrapper id = new ReadOnlyIntegerWrapper(this, "id", 0);
    private final ReadOnlyObjectWrapper<LocalDateTime> serviceDate = new ReadOnlyObjectWrapper<>(this, "serviceDate", null);
    private final ReadOnlyStringWrapper symptoms = new ReadOnlyStringWrapper(this, "symptoms", "");
    private final ReadOnlyStringWrapper diagnosis = new ReadOnlyStringWrapper(this, "diagnosis", "");
    private final ReadOnlyStringWrapper doctorNotes = new ReadOnlyStringWrapper(this, "doctorNotes", "");

    public static ConsultationData FromDb(int id, LocalDateTime serviceDate, String symptoms, String diagnosis, String doctorNotes){
        return new ConsultationData(id, serviceDate, symptoms, diagnosis, doctorNotes);
    }

    private ConsultationData(int id, LocalDateTime serviceDate, String symptoms, String diagnosis, String doctorNotes){
        setId(id);
        setServiceDate(serviceDate);
        setSymptoms(symptoms);
        setDiagnosis(diagnosis);
        setDoctorNotes(doctorNotes);
    }

    public int getId() {
        return id.get();
    }

    public ReadOnlyIntegerProperty idProperty() {
        return id.getReadOnlyProperty();
    }

    private void setId(int id) {
        this.id.set(id);
    }

    public LocalDateTime getServiceDate() {
        return serviceDate.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> serviceDateProperty() {
        return serviceDate.getReadOnlyProperty();
    }

    private void setServiceDate(LocalDateTime serviceDate) {
        this.serviceDate.set(serviceDate);
    }

    public String getSymptoms() {
        return symptoms.get();
    }

    public ReadOnlyStringProperty symptomsProperty() {
        return symptoms.getReadOnlyProperty();
    }

    private void setSymptoms(String symptoms) {
        this.symptoms.set(symptoms);
    }

    public String getDiagnosis() {
        return diagnosis.get();
    }

    public ReadOnlyStringProperty diagnosisProperty() {
        return diagnosis.getReadOnlyProperty();
    }

    private void setDiagnosis(String diagnosis) {
        this.diagnosis.set(diagnosis);
    }

    public String getDoctorNotes() {
        return doctorNotes.get();
    }

    public ReadOnlyStringProperty doctorNotesProperty() {
        return doctorNotes.getReadOnlyProperty();
    }

    private void setDoctorNotes(String doctorNotes) {
        this.doctorNotes.set(doctorNotes);
    }
}
