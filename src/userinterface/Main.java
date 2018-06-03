package userinterface;

import core.model.Account;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import userinterface.navigation.SceneDisplay;
import userinterface.prompt.PromptSource;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application implements LoginHandle.Listener, SceneDisplay, PromptSource {

    static Main instance;

    public static Main getActiveInstance() { return instance; }

    LoginHandle loginHandle;

    Stage mainStage;

    MainView mainView;

    @Override
    public void start(Stage primaryStage) throws Exception{
        instance = this;
        mainStage = primaryStage;
        loginHandle = LoginHandle.getInstance();
        loginHandle.addListener(this);

        mainStage.setHeight(550);
        mainStage.setWidth(800);
        mainStage.setMinHeight(500);
        mainStage.setMinWidth(600);

        showLoginScene();

        mainStage.show();


    }


    public static void main(String[] args) {
        launch(args);
    }


    private Scene getLoginScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/login/login.fxml"));
        Parent root = loader.load();
        return new Scene(root);
    }

    private Scene getMainScene() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainview.fxml"));
        Parent root = loader.load();
        return new Scene(root);
    }

    public MainView getMainView() {
        return mainView;
    }

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    private void showLoginScene() throws IOException{
        display(getLoginScene());
    }

    private void showMainScene() throws IOException {
        display(getMainScene());
    }


    @Override
    public void onLogin(Account account) {
        try {
            showMainScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLogout() {
        try {
            showLoginScene();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void display(Scene item) {
        mainStage.setScene(item);
    }

    @Override
    public Alert createAlertDialog(Alert.AlertType alertType, String promptTitle, String promptMessage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(promptTitle);
        alert.setHeaderText(promptMessage);


        return alert;
    }
}
