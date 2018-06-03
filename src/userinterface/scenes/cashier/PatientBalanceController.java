package userinterface.scenes.cashier;

import core.model.Patient;
import core.model.Service;
import core.util.NumberUtility;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import userinterface.URLSource;
import userinterface.customviewelement.ButtonTableCell;
import userinterface.scenes.BaseController;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class PatientBalanceController extends BaseController {

    public static final String PATIENT_PARAMETER_NAME = "patient";

    @FXML Pane container;

    @FXML Label patientNumberLabel;
    @FXML Label nameLabel;
    @FXML Label birthdayLabel;
    @FXML Label ageLabel;
    @FXML Label genderLabel;
    @FXML Label contactNumberLabel;
    @FXML Label addressLabel;
    @FXML Label nationalityLabel;
    @FXML Label religionLabel;

    @FXML TableView<Service> unpaidServicesTable;

    @FXML TableColumn<Service, Integer> idColumn;
    @FXML TableColumn<Service, String> dateColumn;
    @FXML TableColumn<Service, String> descriptionColumn;
    @FXML TableColumn<Service, String> feeColumn;
    @FXML TableColumn<Service, Button> actionColumn;

    @FXML Label serviceTypeLabel;
    @FXML Label serviceDescriptionLabel;
    @FXML Label serviceFeeLabel;
    @FXML TextField servicePaymentText;


    final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("LLLL dd yyyy hh:mm a");

    final NumberFormat numberFormat = new DecimalFormat("#,##0.00");

    Patient patient;

    ObservableList<Service> services;

    Service currentlyPaying;

    Stage stage;



    @Override public void initialize(URL location, ResourceBundle bundle) {
        super.initialize(location, bundle);
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        container.prefWidthProperty().bind(property);
        initializeTableColumnBindings();

    }

    private void initializeTableColumnBindings() {
        idColumn.setCellValueFactory(new PropertyValueFactory<Service, Integer>("id"));
        dateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Service, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Service, String> param) {
                return Bindings.createStringBinding(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return param.getValue().getServieDate().format(dateTimeFormat);
                    }
                });
            }
        });

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Service, String>("description"));
        feeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Service, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Service, String> param) {
                return Bindings.createStringBinding(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        NumberFormat nf = new DecimalFormat("#,##0.00");
                        return "Php " + nf.format(param.getValue().calculateFee());
                    }
                });
            }
        });
        actionColumn.setCellFactory(ButtonTableCell.<Service>forTableColumn("Pay amount", new Function<Service, Service>() {
            @Override
            public Service apply(Service service) {
                payService(service);
                return service;
            }
        }));
    }

    private void payService(Service service){
        if(service.getPayListener() == null) service.setPayListener(payListener);
        currentlyPaying = service;
        stage = (stage == null) ? new Stage() : stage;
        try {
            stage.setTitle("Service Payment Input");
            FXMLLoader loader = new FXMLLoader(URLSource.getURL("scenes/cashier/paymentinput.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
        bindCurrentPaying();
    }

    @Override protected void onParametersReceived() {
        super.onParametersReceived();
        this.patient = (Patient)parameters.get(PATIENT_PARAMETER_NAME);
        bindPatient(this.patient);
    }

    private void bindPatient(Patient patient) {
        patientNumberLabel.textProperty().bind(patient.idProperty().asString());
        nameLabel.textProperty().bind(patient.fullnameProperty());
        birthdayLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if(patient.getBirthdate() == null) return "";
                DateTimeFormatter format = DateTimeFormatter.ofPattern("LLLL dd yyyy");
                return patient.getBirthdate().format(format);
            }
        }, patient.birthdateProperty()));

        ageLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if(patient.getBirthdate() == null) return "";
                String age;
                Period p = Period.between(patient.getBirthdate(), LocalDate.now());
                if(p.getYears() > 0) age = String.format("%d years old %d months", p.getYears(), p.getMonths());
                else if(p.getMonths() > 0) age = String.format("%d months old %d days", p.getMonths(), p.getDays());
                else age = String.format("%d days", p.getDays());
                return age;
            }
        }, patient.birthdateProperty()));

        genderLabel.textProperty().bind(patient.genderProperty().asString());
        contactNumberLabel.textProperty().bind(patient.contactNumberProperty());
        addressLabel.textProperty().bind(patient.addressProperty());
        nationalityLabel.textProperty().bind(patient.nationalityProperty());
        religionLabel.textProperty().bind(patient.religionProperty());

        unpaidServicesTable.setItems(services = patient.getUnpaidServices());
    }

    private void bindCurrentPaying(){
        serviceTypeLabel.setText(currentlyPaying.getType().name());
        serviceDescriptionLabel.setText(currentlyPaying.getDescription());
        serviceDescriptionLabel.textProperty().bindBidirectional(currentlyPaying.descriptionProperty());
        serviceFeeLabel.setText("Php " + numberFormat.format(currentlyPaying.calculateFee()));
    }

    @FXML private void acceptPaymentAction(ActionEvent event){
        String paymentText = servicePaymentText.getText();
        if(NumberUtility.isNumber(paymentText, true)) {
            double paymentAmount = Double.parseDouble(paymentText);
            currentlyPaying.pay(paymentAmount);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Payment");
            alert.setContentText("Invalid input received.");
            alert.showAndWait();
        }
    }

    private final Service.PayListener payListener = new Service.PayListener() {
        @Override
        public void onServicePaid(Service service, double paymentAmount) {
            services.remove(service);
            if(stage != null) stage.hide();
            double fee = service.calculateFee();
            if(paymentAmount > fee){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Payment");
                alert.setContentText("The change is " + numberFormat.format((paymentAmount - fee)));
                alert.showAndWait();
            }
            currentlyPaying = null;

        }

        @Override
        public void onInsufficientPayment(Service service, double paymentAmount) {
            NumberFormat format = new DecimalFormat("#,##0.00");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Payment");
            alert.setContentText("Insufficient payment for " + service.getType() + " reuired: " + format.format(service.calculateFee()) + " received: " + format.format(paymentAmount) + ".");
            alert.showAndWait();
        }

        @Override
        public void onServiceUnpayable(Service service, String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Service Unpayable");
            alert.setContentText(message);
            alert.showAndWait();
        }
    };
}
