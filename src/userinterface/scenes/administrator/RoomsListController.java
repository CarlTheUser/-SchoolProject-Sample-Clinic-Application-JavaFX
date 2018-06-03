package userinterface.scenes.administrator;

import core.model.Model;
import core.model.Room;
import core.model.RoomType;
import core.model.Validateable;
import core.service.RoomService;
import core.util.NumberUtility;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import userinterface.scenes.BaseController;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.AbstractList;
import java.util.Optional;
import java.util.ResourceBundle;

public class RoomsListController extends BaseController {

    @FXML Pane container;

    @FXML Pane newRoomForm;

    @FXML Button addNewRoomButton;


    @FXML ChoiceBox<RoomType> roomTypesChoiceBox;
    @FXML TextField hourlyRateText;
    @FXML TextField bedCountText;
    @FXML TextArea locationDetailsText;

    @FXML TableView<Room> roomsTable;
    @FXML TableColumn<Room, Integer> roomNumberColumn;
    @FXML TableColumn<Room, RoomType> roomTypeColumn;
    @FXML TableColumn<Room, Double> hourlyRateColumn;
    @FXML TableColumn<Room, Integer> bedCountColumn;
    @FXML TableColumn<Room, String> locationDetailsColumn;



    final SimpleBooleanProperty isAddingRoom = new SimpleBooleanProperty(false);

    final ObservableList<Room> rooms = FXCollections.observableArrayList();

    Room newRoom = null;

    Model selectedModel = null;

    RoomService roomService = new RoomService();

    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        super.initialize(location, bundle);
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        container.prefWidthProperty().bind(property);
        initializeViewComponents();
        initializeTableColumns();
        roomsTable.setItems(rooms);
        rooms.addAll(roomService.getAllRooms());
        roomsTable.getSelectionModel().selectedItemProperty().addListener(tableSelectionListener);
    }

    private void initializeViewComponents() {
        addNewRoomButton.visibleProperty().bind(isAddingRoom.not());
        newRoomForm.visibleProperty().bind(isAddingRoom);
        newRoomForm.managedProperty().bind(newRoomForm.visibleProperty());
        roomTypesChoiceBox.setItems(FXCollections.observableArrayList(RoomType.values()));
    }

    private void initializeTableColumns(){
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<Room, Integer>("id"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<Room, RoomType>("type"));
        hourlyRateColumn.setCellValueFactory(new PropertyValueFactory<Room, Double>("hourlyRate"));
        bedCountColumn.setCellValueFactory(new PropertyValueFactory<Room, Integer>("bedCount"));
        locationDetailsColumn.setCellValueFactory(new PropertyValueFactory<Room, String>("locationDetails"));

        hourlyRateColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        bedCountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        roomTypeColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(FXCollections.observableArrayList(RoomType.values())));
        locationDetailsColumn.setCellFactory(TextFieldTableCell.forTableColumn());


    }


    private final ChangeListener<Model> tableSelectionListener = new ChangeListener<Model>() {
        @Override
        public void changed(ObservableValue<? extends Model> observable, Model oldValue, Model newValue) {
            if(newValue != null) attachEventListeners(selectedModel = newValue);

        }
    };

    private void attachEventListeners(Model model) {
        model.setEditListener(editListener);
        model.setValidationListener(validationListener);
        model.setErrorListener(errorListener);
    }

    private void detachEventListeners(Model model){
        model.setEditListener(null);
        model.setValidationListener(null);
        model.setErrorListener(null);
    }


    @FXML void roomTypeColumnEditStartEvent(TableColumn.CellEditEvent event){
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        selected.beginEdit();
    }

    @FXML void roomTypeColumnEditCommitEvent(TableColumn.CellEditEvent event){
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        selected.setType((RoomType) event.getNewValue());
        selected.applyEdit();
    }

    @FXML void roomTypeColumnEditCancelEvent(TableColumn.CellEditEvent event){
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        selected.cancelEdit();
    }

    @FXML void hourlyRateColumnEditStartEvent(TableColumn.CellEditEvent event){
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        selected.beginEdit();
    }

    @FXML void hourkyRateColumnEditCommitEvent(TableColumn.CellEditEvent event){
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        String number = event.getNewValue().toString();
        if(NumberUtility.isNumber(number, true) && number.length() < 10)
            selected.setHourlyRate(Double.parseDouble(number));
        selected.applyEdit();
    }

    @FXML void hourlyRateEditCancelEvent(TableColumn.CellEditEvent event){
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        selected.cancelEdit();
    }

    @FXML void bedCountColumnEditStartEvent(TableColumn.CellEditEvent event){
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        selected.beginEdit();
    }

    @FXML void bedCountColumnEditCommitEvent(TableColumn.CellEditEvent event){
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        String number = event.getNewValue().toString();
        if(NumberUtility.isNumber(number, false) && number.length() < 10)
            selected.setBedCount(Integer.parseInt(number));
        selected.applyEdit();
    }

    @FXML void bedCountColumnEditCancelEvent(TableColumn.CellEditEvent event){
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        selected.cancelEdit();
    }

    @FXML void locationDetailsColumnEditStartEvent(TableColumn.CellEditEvent event){
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        selected.beginEdit();
    }

    @FXML void locationDetailsColumnEditCommitEvent(TableColumn.CellEditEvent event){
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        selected.setLocationDetails(event.getNewValue().toString());
        selected.applyEdit();
    }

    @FXML void locationDetailsColumnEditCancelEvent(TableColumn.CellEditEvent event){
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        selected.cancelEdit();
    }

    private void initializeNewRoom(){
        newRoom = null;
        newRoom = Room.NewInstance(saveListener, validationListener, errorListener);
        roomTypesChoiceBox.valueProperty().bindBidirectional(newRoom.typeProperty());
        hourlyRateText.textProperty().bindBidirectional(newRoom.hourlyRateProperty(), new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                NumberFormat numberFormat = new DecimalFormat("###.##");
                return numberFormat.format(object);
            }

            @Override
            public Number fromString(String string) {
                return NumberUtility.isNumber(string, true) ? Double.parseDouble(string) : 0;
            }
        });
        bedCountText.textProperty().bindBidirectional(newRoom.bedCountProperty(), new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return new DecimalFormat("#").format(object);
            }

            @Override
            public Number fromString(String string) {
                return NumberUtility.isNumber(string) ? Integer.parseInt(string) : 0;
            }
        });
        locationDetailsText.textProperty().bindBidirectional(newRoom.locationDetailsProperty());
    }

    private void cleanupNewRoom(){
        newRoom.setSaveListener(null);
        newRoom.setValidationListener(null);
        newRoom.setErrorListener(null);
        roomTypesChoiceBox.valueProperty().unbindBidirectional(newRoom.typeProperty());
        hourlyRateText.textProperty().unbindBidirectional(newRoom.hourlyRateProperty());
        bedCountText.textProperty().unbindBidirectional(newRoom.bedCountProperty());
        locationDetailsText.textProperty().unbindBidirectional(newRoom.locationDetailsProperty());
        newRoom = null;
    }

    @FXML private void newRoomAction(ActionEvent event){
        initializeNewRoom();
        isAddingRoom.setValue(true);
    }

    @FXML private void cancelNewRoomAction(ActionEvent event){
        isAddingRoom.setValue(false);
        cleanupNewRoom();
    }

    @FXML private void saveNewRoomAction(ActionEvent event){
        newRoom.save();
    }

    private final Model.SaveListener saveListener = new Model.SaveListener() {
        @Override
        public void onSaved() {
            rooms.add(newRoom);
            cleanupNewRoom();
            newRoom = null;
            isAddingRoom.setValue(false);
        }
    };

    private final Model.EditListener editListener = new Model.EditListener() {
        @Override
        public void onEditBegun() {
            System.out.println("EditBegun called on Selected room" + ((Room)selectedModel).getType());
        }

        @Override
        public void onEditCancelled() {
            System.out.println("EditCancelled called on Selected room" + ((Room)selectedModel).getType());
        }

        @Override
        public void onEditApplied() {
            System.out.println("EditApplied called on Selected room" + ((Room)selectedModel).getType());
        }
    };

    private final Validateable.ValidationListener validationListener = new Validateable.ValidationListener() {
        @Override
        public void onValidated(AbstractList<String> brokenRules) {
            if(brokenRules.size() > 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation error");
                alert.setContentText(brokenRules.get(0));
                alert.showAndWait();
            }

        }
    };

    private final Model.ErrorListener errorListener = new Model.ErrorListener() {
        @Override
        public void onError(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation error");
            alert.setContentText(message);
            alert.showAndWait();
        }
    };

}
