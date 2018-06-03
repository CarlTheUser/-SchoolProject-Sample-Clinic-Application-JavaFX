package core.model.readonly;

import core.model.Patient;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.time.LocalDateTime;

public class PatientVisit {
    private final ReadOnlyObjectWrapper<Patient> patient = new ReadOnlyObjectWrapper<>(this, "patient", null);
    private final ReadOnlyObjectWrapper<LocalDateTime> visitDate = new ReadOnlyObjectWrapper<>(this, "visitDate", null);

    public static PatientVisit FromDb(Patient patient, LocalDateTime visitDate){
        return new PatientVisit(patient, visitDate);
    }

    private PatientVisit(Patient patient, LocalDateTime visitDate){
        this.patient.set(patient);
        this.visitDate.set(visitDate);
    }

    public Patient getPatient() {
        return patient.get();
    }

    public ReadOnlyObjectProperty<Patient> patientProperty() {
        return patient.getReadOnlyProperty();
    }

    public LocalDateTime getVisitDate() {
        return visitDate.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> visitDateProperty() {
        return visitDate.getReadOnlyProperty();
    }
}
