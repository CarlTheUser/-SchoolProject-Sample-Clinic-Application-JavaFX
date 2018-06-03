package userinterface.navigationview;

import core.model.Account;
import core.model.StaffInformation;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import userinterface.Clock;
import userinterface.LoginHandle;
import userinterface.Main;
import userinterface.URLSource;
import userinterface.navigation.UserNavigation;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class NavigationController implements Initializable {

    @FXML Label labelDate;
    @FXML Label labelTime;
    @FXML Label staffNameLabel;
    @FXML Label accountTypeLabel;

    private StaffInformation staffInformation;

    private UserNavigation navigation;

    public UserNavigation getNavigation() {
        return navigation;
    }

    public void setNavigation(UserNavigation navigation) {
        this.navigation = navigation;
    }

    @Override public void initialize(URL location, ResourceBundle resources) {
        Clock clock = new Clock(1000 * 60);
        clock.setListener(cloclListener);
        Account account = LoginHandle.getInstance().getCurrentAccount();
        accountTypeLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Account account = LoginHandle.getInstance().getCurrentAccount();
                return account.getAccountType().name();
            }
        }, account.accountTypeProperty()));
        staffInformation = account.getStaffInformation();
        staffNameLabel.textProperty().bind(staffInformation.fullnameProperty());
    }

    public StaffInformation getStaffInformation() {
        return staffInformation;
    }

    public void setStaffInformation(StaffInformation staffInformation) {
        this.staffInformation = staffInformation;
    }


    @FXML private void logoutAction(ActionEvent action){
        LoginHandle.getInstance().logOut();
    }

    @FXML private void backAction(ActionEvent action){
        navigation.navigateBack();
    }


    @FXML private void receptionistNewPatientAction(ActionEvent action){
        URL url = URLSource.getURL("scenes/receptionist/newpatient.fxml");
        navigation.navigate(new UserNavigation.NavigationItem(url, false));
    }

    @FXML private void receptionistViewPatientsAction(ActionEvent action){
        URL url = URLSource.getURL("scenes/receptionist/patientslist.fxml");
        navigation.navigate(new UserNavigation.NavigationItem(url, true));
    }

    @FXML private void receptionistViewAdmittedPatientsAction(ActionEvent action){
        URL url = URLSource.getURL("scenes/receptionist/admittedpatientslist.fxml");
        navigation.navigate(new UserNavigation.NavigationItem(url, true));
    }

    @FXML private void doctorViewCurrentVisitsAction(ActionEvent event){
        URL url = URLSource.getURL("scenes/doctor/currentvisitslist.fxml");
        navigation.navigate(new UserNavigation.NavigationItem(url, true));
    }

    @FXML private void doctorViewConsultationLogsAction(ActionEvent event){
        URL url = URLSource.getURL("scenes/doctor/consultationlogs.fxml");
        navigation.navigate(new UserNavigation.NavigationItem(url, true));
    }

    @FXML private void administratorManageStaffAction(ActionEvent event){
        URL url = URLSource.getURL("scenes/administrator/stafflist.fxml");
        navigation.navigate(new UserNavigation.NavigationItem(url, true));
    }

    @FXML private void administratorManageRoomsAction(ActionEvent event){
        URL url = URLSource.getURL("scenes/administrator/roomslist.fxml");
        navigation.navigate(new UserNavigation.NavigationItem(url, true));
    }

    @FXML private void administratorDoctorPatientsReportAction(ActionEvent event){
        URL url = URLSource.getURL("scenes/administrator/doctorpatientvisitreport.fxml");
        navigation.navigate(new UserNavigation.NavigationItem(url, true));
    }

    @FXML private void cashierPatientsListAction(ActionEvent event){
        URL url = URLSource.getURL("scenes/cashier/patientlist.fxml");
        navigation.navigate(new UserNavigation.NavigationItem(url, true));
    }



    private Clock.Listener cloclListener = new Clock.Listener() {
        @Override
        public void onClockUpdated(LocalDateTime currentTime) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd yyyy");
                    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
                    labelDate.setText(currentTime.format(dateFormat));
                    labelTime.setText(currentTime.format(timeFormat));
                }
            });
        }
    };
}
