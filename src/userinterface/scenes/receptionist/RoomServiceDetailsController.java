package userinterface.scenes.receptionist;

import core.model.Model;
import core.model.Room;
import core.model.RoomService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import userinterface.scenes.BaseController;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

public class RoomServiceDetailsController extends BaseController {

    @FXML Label roomNumberLabel;
    @FXML Label roomTypeLabel;
    @FXML Label hourlyRateLabel;
    @FXML Label locationDetailsLabel;
    @FXML Label dateInLabel;
    @FXML Label hoursOfStayLabel;
    @FXML Label dateOutLabel;

    @FXML Button patientLeaveRoomButton;

    @FXML Pane dateOutContainer;

    RoomService roomService;

    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("LLLL dd yyyy hh:mm a");

    private final SimpleBooleanProperty isPatientLeft = new SimpleBooleanProperty(false);

    public RoomService getRoomService() {
        return roomService;
    }

    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
        roomService.refresh();
        bindRoomService(this.roomService);
    }

    private void bindRoomService(RoomService roomService) {
        roomService.setEditListener(editListener);
        roomService.setErrorListener(errorListener);
        Room room = roomService.getRoom();
        roomNumberLabel.textProperty().bind(room.idProperty().asString());
        roomTypeLabel.textProperty().bind(room.typeProperty().asString());
        hourlyRateLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return new DecimalFormat("#,##0.00").format(room.getHourlyRate());
            }
        }));
        locationDetailsLabel.textProperty().bind(room.locationDetailsProperty());
        dateInLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return roomService.getDateIn().format(dateTimeFormat);
            }
        }));
        hoursOfStayLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                LocalDateTime dateOut = roomService.getDateOut() == null ? LocalDateTime.now() : roomService.getDateOut();
                LocalDateTime dateIn = roomService.getDateIn();
                Duration duration = Duration.between(dateIn, dateOut);
                long totalHours  = duration.toHours() > 0 ? duration.toHours() : 1;
                return Long.toString(totalHours) + " hours";
            }
        }));
        dateOutContainer.visibleProperty().bind(isPatientLeft);
        dateOutContainer.managedProperty().bind(dateOutContainer.visibleProperty());
        patientLeaveRoomButton.visibleProperty().bind(isPatientLeft.not());
        patientLeaveRoomButton.managedProperty().bind(patientLeaveRoomButton.visibleProperty());

        isPatientLeft.addListener(changeListener);
        isPatientLeft.set(roomService.getDateOut() != null);
    }

    private final ChangeListener<Boolean> changeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if(newValue){
                dateOutLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return roomService.getDateOut().format(dateTimeFormat);
                    }
                }));
            }
        }
    };

    @FXML private void patientLeaveRoomAction(ActionEvent event){
        roomService.beginEdit();
        roomService.setDateOut(LocalDateTime.now());
        roomService.applyEdit();
    }

    private final Model.EditListener editListener = new Model.EditListener() {
        @Override
        public void onEditBegun() {

        }

        @Override
        public void onEditCancelled() {

        }

        @Override
        public void onEditApplied() {
            isPatientLeft.set(true);
        }
    };

    private final Model.ErrorListener errorListener = new Model.ErrorListener() {
        @Override
        public void onError(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("An error occurred");
            alert.setContentText(message);
            alert.showAndWait();
        }
    };

}
