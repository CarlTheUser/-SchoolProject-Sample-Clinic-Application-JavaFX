package core.model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractList;

public class LaboratoryService extends Service {
    private final SimpleStringProperty findings = new SimpleStringProperty(this, "findings", "");

    private final SimpleObjectProperty<LocalDate> resultDate = new SimpleObjectProperty<>(this, "resultDate", LocalDate.now());



    private LaboratoryService(VisitRecord visitRecord, SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener) {
        super(visitRecord, LocalDateTime.now(), saveListener, validationListener, errorListener);
    }

    @Override
    public ServiceType getType() {
        return ServiceType.LaboratoryService;
    }

    @Override
    public double calculateFee() {
        return getFee();
    }

    @Override
    public ValidationCriterion isPayable() {
        return null;
    }

    public String getFindings() {
        return findings.get();
    }

    public SimpleStringProperty findingsProperty() {
        return findings;
    }

    public void setFindings(String findings) {
        this.findings.set(findings);
    }

    public LocalDate getResultDate() {
        return resultDate.get();
    }

    public SimpleObjectProperty<LocalDate> resultDateProperty() {
        return resultDate;
    }

    public void setResultDate(LocalDate resultDate) {
        this.resultDate.set(resultDate);
    }

    private String findingsBackup = null;
    private LocalDate resultDateBackup = null;

    @Override
    protected void backupFields() {
        super.backupFields();
        findingsBackup = getFindings();
        resultDateBackup = getResultDate();
    }

    @Override
    protected void restoreFields() {
        super.restoreFields();
        setFindings(findingsBackup);
        findingsBackup = null;
        setResultDate(resultDateBackup);
        resultDateBackup = null;
    }

    @Override
    protected void saveMethod() {

    }

    @Override
    protected void updateMethod() {

    }

    @Override
    protected void deleteMethod() {

    }

    @Override
    protected void refreshMethod() {

    }

    @Override
    protected AbstractList<ValidationCriterion> createValidationCriteria() {
        return super.createValidationCriteria();
    }
}
