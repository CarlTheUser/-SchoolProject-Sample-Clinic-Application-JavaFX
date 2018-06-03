package core.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class OperationService extends Service {

    private final SimpleStringProperty notes = new SimpleStringProperty(this, "notes", "");
    private final ObservableList<StaffInformation> localOperatingStaffs = FXCollections.observableArrayList();
    private final ObservableList<StaffInformation> operatingStaffs = FXCollections.unmodifiableObservableList(localOperatingStaffs);

    private boolean doctorsLoaded = false;

    private OperationService(VisitRecord visitRecord, SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener) {
        super(visitRecord, LocalDateTime.now(), saveListener, validationListener, errorListener);
    }


    private void loadDoctors(){

    }

    public ObservableList<StaffInformation> getOperatingStaffs() {
        if(!doctorsLoaded){
            loadDoctors();
            doctorsLoaded = true;
        }
        return operatingStaffs;
    }


    @Override
    public ServiceType getType() {
        return ServiceType.OperationService;
    }

    @Override
    public double calculateFee() {
        return getFee();
    }

    @Override
    public ValidationCriterion isPayable() {
        return null;
    }

    public String getNotes() {
        return notes.get();
    }

    public SimpleStringProperty notesProperty() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public void addDoctor(StaffInformation staff){
        localOperatingStaffs.add(staff);
    }

    @Override
    protected void backupFields() {
        super.backupFields();
    }

    @Override
    protected void restoreFields() {
        super.restoreFields();
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
        AbstractList<ValidationCriterion> superCriteria = super.createValidationCriteria();
        List<ValidationCriterion> validationCriteria = new ArrayList<>();
        if(superCriteria != null) validationCriteria.addAll(superCriteria);

        validationCriteria.add(new ValidationCriterion("Select staff involved in the operation.") {
            @Override
            public boolean validate() {
                return localOperatingStaffs.size() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Received an invalid item in the list of staff.") {
            @Override
            public boolean validate() {
                for(StaffInformation staff : localOperatingStaffs){
                    if (!staff.isIdentifiable()) return false;
                }
                return true;
            }
        });

        return new AbstractList<ValidationCriterion>() {
            @Override
            public ValidationCriterion get(int index) {
                return validationCriteria.get(index);
            }

            @Override
            public int size() {
                return validationCriteria.size();
            }
        };
    }
}
