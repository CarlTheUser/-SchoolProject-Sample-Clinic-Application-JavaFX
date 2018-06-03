package data.model;

import java.time.LocalDate;

public class VisitRecord {
    int id;
    int patientId;
    String patientFirstname;
    String patientMiddlename;
    String patientLastname;
    String notes;
    LocalDate date;

    public VisitRecord(){}

    public VisitRecord(int patientId, String notes, LocalDate date) {
        this(0, patientId,  notes, date);
    }

    public VisitRecord(int id, int patientId, String notes, LocalDate date) {
       this(id, patientId, null, null, null, notes,date);
    }

    public VisitRecord(int id, int patientId, String patientFirstname, String patientMiddlename, String patientLastname, String notes, LocalDate date) {
        this.id = id;
        this.patientId = patientId;
        this.patientFirstname = patientFirstname;
        this.patientMiddlename = patientMiddlename;
        this.patientLastname = patientLastname;
        this.notes = notes;
        this.date = date;
    }
}
