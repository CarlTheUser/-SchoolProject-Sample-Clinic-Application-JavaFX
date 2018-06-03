package core.model.readonly;

import core.model.Patient;
import core.model.StaffInformation;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.time.LocalDateTime;

public class DoctorPatientVisit {
    final ReadOnlyObjectWrapper<StaffInformation> doctor = new ReadOnlyObjectWrapper<>(this, "doctor", null);
    final ReadOnlyObjectWrapper<Patient> patient = new ReadOnlyObjectWrapper<>(this, "patient", null);
    final ReadOnlyObjectWrapper<LocalDateTime> lastVisit = new ReadOnlyObjectWrapper<>(this, "lastVisit", null);


    public static DoctorPatientVisit FromDb(StaffInformation doctor, Patient patient, LocalDateTime lastVisit) {
        return new DoctorPatientVisit(doctor, patient, lastVisit);
    }

    private DoctorPatientVisit(StaffInformation doctor, Patient patient, LocalDateTime lastVisit) {
        setDoctor(doctor);
        setPatient(patient);
        setLastVisit(lastVisit);
    }

    public StaffInformation getDoctor() {
        return doctor.get();
    }

    public ReadOnlyObjectProperty<StaffInformation> doctorProperty() {
        return doctor.getReadOnlyProperty();
    }

    private void setDoctor(StaffInformation doctor) {
        this.doctor.set(doctor);
    }

    public Patient getPatient() {
        return patient.get();
    }

    public ReadOnlyObjectProperty<Patient> patientProperty() {
        return patient.getReadOnlyProperty();
    }

    private void setPatient(Patient patient) {
        this.patient.set(patient);
    }

    public LocalDateTime getLastVisit() {
        return lastVisit.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> lastVisitProperty() {
        return lastVisit.getReadOnlyProperty();
    }

    private void setLastVisit(LocalDateTime lastVisit) {
        this.lastVisit.set(lastVisit);
    }
}
