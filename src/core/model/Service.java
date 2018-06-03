package core.model;

import core.service.VisitRecordService;
import data.Database;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public abstract class Service extends Model {

    protected final SimpleIntegerProperty id = new SimpleIntegerProperty(this, "id", 0);
    protected final SimpleIntegerProperty serviceId = new SimpleIntegerProperty(this, "servieId", 0);
    protected final SimpleObjectProperty<VisitRecord> visitRecord = new SimpleObjectProperty<>(this, "visitRecord", null);
    protected final SimpleStringProperty description = new SimpleStringProperty(this, "description", "");
    protected final SimpleDoubleProperty fee = new SimpleDoubleProperty(this, "fee", 0);
    protected final SimpleDoubleProperty payment = new SimpleDoubleProperty(this, "payment", 0);
    protected final SimpleObjectProperty<LocalDateTime> servieDate = new SimpleObjectProperty<>(this, "serviceDate", LocalDateTime.now());

    private PayListener payListener;

    protected Service(VisitRecord visitRecord, LocalDateTime serviceDate, SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener){
        setVisitRecord(visitRecord);
        setServieDate(serviceDate);
        setSaveListener(saveListener);
        setValidationListener(validationListener);
        setErrorListener(errorListener);
    }

    private boolean visitRecordLoaded = false;

    private void loadVisitRecord(){
        VisitRecordService service = new VisitRecordService();
        VisitRecord v = service.getVisitRecordByService(this);
        setVisitRecord(v);
    }

    public abstract ServiceType getType();

    public abstract double calculateFee();

    public abstract ValidationCriterion isPayable();

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getServiceId() {
        return serviceId.get();
    }

    public SimpleIntegerProperty serviceIdProperty() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId.set(serviceId);
    }

    public VisitRecord getVisitRecord() {
        if(!visitRecordLoaded){
            loadVisitRecord();
            visitRecordLoaded = true;
        }
        return visitRecord.get();
    }

    public SimpleObjectProperty<VisitRecord> visitRecordProperty() {
        return visitRecord;
    }

    public void setVisitRecord(VisitRecord visitRecord) {
        if(visitRecord == null) return;
        this.visitRecord.set(visitRecord);
        visitRecordLoaded = true;
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public double getFee() {
        return fee.get();
    }

    public SimpleDoubleProperty feeProperty() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee.set(fee);
    }

    public double getPayment() {
        return payment.get();
    }

    public SimpleDoubleProperty paymentProperty() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment.set(payment);
    }

    public LocalDateTime getServieDate() {
        return servieDate.get();
    }

    public SimpleObjectProperty<LocalDateTime> servieDateProperty() {
        return servieDate;
    }

    public void setServieDate(LocalDateTime servieDate) {
        this.servieDate.set(servieDate);
    }

    public void pay(double paymentAmount){
        ValidationCriterion isPayable = isPayable();
        if(isPayable == null || isPayable.validate()) {
            double fee = calculateFee();
            if (paymentAmount >= fee) {
                Connection connection = Database.createConnection();
                try {
                    PreparedStatement statement = connection.prepareStatement("Update Service Set Payment = ? Where Id = ?");
                    statement.setDouble(1, paymentAmount);
                    statement.setInt(2, getId());
                    statement.executeUpdate();
                    if (payListener != null) payListener.onServicePaid(this, paymentAmount);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    Database.closeDatabaseResource(connection);
                }
            } else {
                if (payListener != null) payListener.onInsufficientPayment(this, paymentAmount);
            }
        } else {
            if(payListener != null) payListener.onServiceUnpayable(this, isPayable.getErrorMessage());
        }
    }

    public PayListener getPayListener() {
        return payListener;
    }

    public void setPayListener(PayListener payListener) {
        this.payListener = payListener;
    }

    private String descriptionBackup = null;
    private Double feeBackup = null;
    private Double paymentBackup = null;

    @Override protected void backupFields() {
        descriptionBackup = getDescription();
        feeBackup = getFee();
        paymentBackup = getPayment();
    }

    @Override protected void restoreFields() {
        setDescription(descriptionBackup);
        setFee(feeBackup);
        setPayment(paymentBackup);
    }

    @Override
    protected void clearBackup() {
        descriptionBackup = null;
        feeBackup = null;
        paymentBackup = null;
    }

    @Override protected boolean isIdentifiable() {
        return getId() > 0 && getServiceId() > 0;
    }

    @Override protected AbstractList<ValidationCriterion> createValidationCriteria() {
        List<ValidationCriterion> validationCriteria = new ArrayList<>();

        validationCriteria.add(new ValidationCriterion("Reference to patient visit is invalid.") {
            @Override public boolean validate() {
                return getVisitRecord() != null && getVisitRecord().isIdentifiable();
            }
        });
        validationCriteria.add(new ValidationCriterion("Reference to patient is invalid.") {
            @Override public boolean validate() {
                VisitRecord v = getVisitRecord();
                return v.getPatient() != null && v.getPatient().isIdentifiable();
            }
        });

        validationCriteria.add(new ValidationCriterion("Service fee amount is negative.") {
            @Override public boolean validate() {
                return getFee() >= 0;
            }
        });

        return new AbstractList<ValidationCriterion>() {
            @Override public ValidationCriterion get(int index) {
                return validationCriteria.get(index);
            }

            @Override public int size() {
                return validationCriteria.size();
            }
        };
    }



    public interface PayListener{
        public void onServicePaid(Service service, double paymentAmount);
        public void onInsufficientPayment(Service service, double paymentAmount);
        public void onServiceUnpayable(Service service, String message);
    }
}
