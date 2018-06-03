package core.model;

import data.Database;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class MedicineAndEquipmentService extends Service {

    private final SimpleStringProperty item = new SimpleStringProperty(this, "item", "");
    private final SimpleIntegerProperty quantity = new SimpleIntegerProperty(this, "quantity", 0);
    private final SimpleDoubleProperty unitPrice = new SimpleDoubleProperty(this, "unitPrice", 0);

    public static MedicineAndEquipmentService NewInstance(VisitRecord visitRecord, LocalDateTime serviceDate, SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener) {
        MedicineAndEquipmentService m = new MedicineAndEquipmentService(visitRecord, serviceDate, saveListener, validationListener, errorListener);
        m.isNew = true;
        m.unitPriceProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                m.feeProperty().setValue(newValue.doubleValue() * m.getQuantity());
            }
        });
        return m;
    }

    public static MedicineAndEquipmentService FromDb(int id, VisitRecord visitRecord, String description, int serviceId, double fee, LocalDateTime serviceDate){
        MedicineAndEquipmentService m = new MedicineAndEquipmentService(visitRecord, serviceDate, null, null, null);
        m.setId(id);
        m.setDescription(description);
        m.setServiceId(serviceId);
        m.setFee(fee);
        m.isNew = false;
        return m;
    }

    protected MedicineAndEquipmentService(VisitRecord visitRecord, LocalDateTime serviceDate, SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener) {
        super(visitRecord, serviceDate, saveListener, validationListener, errorListener);
    }



    @Override
    public ServiceType getType() {
        return ServiceType.MedicineAndEquipmentService;
    }

    @Override
    public double calculateFee() {
        return getFee();
    }

    @Override
    public ValidationCriterion isPayable() {
        return null;
    }

    public String getItem() {
        return item.get();
    }

    public SimpleStringProperty itemProperty() {
        return item;
    }

    public void setItem(String item) {
        this.item.set(item);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public double getUnitPrice() {
        return unitPrice.get();
    }

    public SimpleDoubleProperty unitPriceProperty() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice.set(unitPrice);
    }

    @Override
    protected void saveMethod() throws SQLException {
        Connection connection = Database.createConnection();
        PreparedStatement serviceStatement= null;
        PreparedStatement medicineServiceStatement = null;
        try {
            connection.setAutoCommit(false);
            medicineServiceStatement = connection.prepareStatement("Insert Into MedicineEquipmentServices(Item, Quantity) Values (?, ?)", Statement.RETURN_GENERATED_KEYS);
            medicineServiceStatement.setString(1,getItem());
            medicineServiceStatement.setInt(2, getQuantity());
            medicineServiceStatement.executeUpdate();
            ResultSet medicineKeyResultSet = medicineServiceStatement.getGeneratedKeys();
            if(medicineKeyResultSet.next()){
                int id = medicineKeyResultSet.getInt(1);
                setServiceId(id);
            }
            serviceStatement = connection.prepareStatement("Insert Into Service(VisitId, ServiceType, Description, ServiceId, Fee, ServiceDate) Values(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            serviceStatement.setInt(1, getVisitRecord().getId());
            serviceStatement.setInt(2, getType().getValue());
            serviceStatement.setString(3, getDescription());
            serviceStatement.setInt(4, getServiceId());
            serviceStatement.setDouble(5, getFee());
            serviceStatement.setTimestamp(6, Timestamp.valueOf(getServieDate()));
            serviceStatement.executeUpdate();
            ResultSet serviceKeyResultSet = serviceStatement.getGeneratedKeys();
            if(serviceKeyResultSet.next()){
                int id = serviceKeyResultSet.getInt(1);
                setId(id);
            }

            connection.commit();
        } catch (SQLException e){
            connection.rollback();
            throw e;
        } finally {
            Database.closeDatabaseResource(connection, serviceStatement, medicineServiceStatement);
        }
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

        validationCriteria.add(new ValidationCriterion("Item name missing.") {
            @Override
            public boolean validate() {
                return getItem().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Item quantity was not supplied.") {
            @Override
            public boolean validate() {
                return getQuantity() > 0;
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
