package data.model;

import java.time.LocalDate;

public class Admission {

    int visitId;
    int patientId;
    String patientFirstname;
    String patientMiddlename;
    String patientLastname;
    String initialFindings;
    String finalFindings;
    LocalDate admissionDate;
    LocalDate dischargeDate;

    public Admission(int visitId, String initialFindings, String finalFindings, LocalDate admissionDate, LocalDate dischargeDate) {
        this(visitId, 0, null, null, null, initialFindings, finalFindings, admissionDate, dischargeDate);
    }

    public Admission(int visitId, int patientId, String patientFirstname, String patientMiddlename, String patientLastname, String initialFindings, String finalFindings, LocalDate admissionDate, LocalDate dischargeDate) {
        this.visitId = visitId;
        this.patientId = patientId;
        this.patientFirstname = patientFirstname;
        this.patientMiddlename = patientMiddlename;
        this.patientLastname = patientLastname;
        this.initialFindings = initialFindings;
        this.finalFindings = finalFindings;
        this.admissionDate = admissionDate;
        this.dischargeDate = dischargeDate;
    }

    public int getVisitId() {
        return visitId;
    }

    public void setVisitId(int visitId) {
        this.visitId = visitId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getPatientFirstname() {
        return patientFirstname;
    }

    public void setPatientFirstname(String patientFirstname) {
        this.patientFirstname = patientFirstname;
    }

    public String getPatientMiddlename() {
        return patientMiddlename;
    }

    public void setPatientMiddlename(String patientMiddlename) {
        this.patientMiddlename = patientMiddlename;
    }

    public String getPatientLastname() {
        return patientLastname;
    }

    public void setPatientLastname(String patientLastname) {
        this.patientLastname = patientLastname;
    }

    public String getInitialFindings() {
        return initialFindings;
    }

    public void setInitialFindings(String initialFindings) {
        this.initialFindings = initialFindings;
    }

    public String getFinalFindings() {
        return finalFindings;
    }

    public void setFinalFindings(String finalFindings) {
        this.finalFindings = finalFindings;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }

    public LocalDate getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(LocalDate dischargeDate) {
        this.dischargeDate = dischargeDate;
    }
}
