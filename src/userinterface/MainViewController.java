package userinterface;

import core.model.AccountType;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import userinterface.navigation.NodeDisplay;
import userinterface.navigation.UserNavigation;
import userinterface.navigation.UserNavigation.NavigationListener.PreNavigationArgument;
import userinterface.navigationview.NavigationController;
import userinterface.scenes.BaseController;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.scene.layout.BackgroundPosition.CENTER;
import static javafx.scene.layout.BackgroundRepeat.NO_REPEAT;
import static javafx.scene.layout.BackgroundRepeat.REPEAT;
import static javafx.scene.layout.BackgroundSize.*;

public class MainViewController extends BaseController implements MainView, UserNavigation {

    @FXML BorderPane contentPane;

    ScrollPane scrollPane;
    NodeDisplay paneDisplay;

    Stack<NavigationItem> navigationBackStack = new Stack<>();
    NavigationItem currentNavigation;

    Vector<NavigationListener> listeners = new Vector<>();

    @Override public void initialize(URL location, ResourceBundle resources) {
        displayUserNavigationPane();
        DisplayScrollPane displayScrollPane = new DisplayScrollPane();
        scrollPane = displayScrollPane;
        paneDisplay = displayScrollPane;
        contentPane.setCenter(displayScrollPane);
        Main.getActiveInstance().setMainView(this);
        scrollPane.getStylesheets().add(this.getClass()
                .getResource("style/style.css").toExternalForm());
        navigate(new NavigationItem(URLSource.getURL("blank.fxml")));
//        contentPane.setBackground(new Background(new BackgroundImage(new Image("@../images/hospital.jpg"), NO_REPEAT, NO_REPEAT, CENTER, DEFAULT)));
    }

    @Override public UserNavigation getUserNavigation() {
        return this;
    }

    @Override public void addNavigationListener(NavigationListener listener) {
        listeners.add(listener);
    }

    @Override public void removeNavigationListener(NavigationListener listener) {
        listeners.remove(listener);
    }

    @Override public ReadOnlyDoubleProperty getDisplayerWidthProperty() {
        return scrollPane.widthProperty();
    }

    @Override public ReadOnlyDoubleProperty getDisplayerHeightProperty() {
        return scrollPane.heightProperty();
    }

    private void resetViewScroll(){
        scrollPane.setVvalue(0);
        scrollPane.setHvalue(0);
    }

    private boolean cancelNavigation(URL url){
        Boolean cancel = false;
        NavigationListener[] navigationListeners = new NavigationListener[listeners.size()];
        listeners.copyInto(navigationListeners);
        PreNavigationArgument argument = new PreNavigationArgument(url);
        for (NavigationListener listener : navigationListeners) {
            listener.onPreNavigate(argument);
            if (cancel = argument.isCancelNavigate()) break;
        }
        return cancel;
    }

    @Override public boolean navigate(NavigationItem navigationItem) {
        if(currentNavigation != null){
            if(currentNavigation.getNavigationUrl().sameFile(navigationItem.getNavigationUrl())) return false;
        }
        if(!cancelNavigation(navigationItem.getNavigationUrl())){
            try{
                FXMLLoader loader = new FXMLLoader(navigationItem.getNavigationUrl());
                Node node = loader.load();
                if(navigationItem.getParameters() != null){
                    BaseController controller = (BaseController)loader.getController();
                    controller.setParameters(navigationItem.getParameters());
                }
                paneDisplay.display(node);
                currentNavigation = navigationItem;
                if(navigationItem.isAddToBackStack()) navigationBackStack.push(navigationItem);
                return true;
            } catch (IOException e){
                e.printStackTrace();
                return false;
            }
        } else return false;
    }

    @Override
    public void navigateHome() {
        if(navigate(new NavigationItem(URLSource.getURL("blank.fxml")))){
            navigationBackStack.clear();
            navigationBackStack.push(currentNavigation);
        }
    }

    @Override public void navigateBack() {
        NavigationItem current = null;
        if(currentNavigation != null) {
            if (currentNavigation.isAddToBackStack()) {
                current = navigationBackStack.pop();
                currentNavigation = null;
            }
        }
        if(navigationBackStack.size() > 0){
            NavigationItem last = navigationBackStack.pop();
            if(!navigate(last)){
                navigationBackStack.push(last);
            }
        }
    }

    private Pane getBlankPane() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("blank.fxml"));
        Pane pane = loader.load();
        return pane;
    }

    private Pane getReceptionistNavigationPane() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("navigationview/receptionistnavigation.fxml"));
        Pane pane = loader.load();
        NavigationController controller = loader.getController();
        controller.setStaffInformation(null);
        controller.setNavigation(this);
        return pane;
    }

    private Pane getDoctorNavigationPane() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("navigationview/doctornavigation.fxml"));
        Pane pane = loader.load();
        NavigationController controller = loader.getController();
        controller.setStaffInformation(null);
        controller.setNavigation(this);
        return pane;
    }

    private Pane getCashierNavigationPane() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("navigationview/cashiernavigation.fxml"));
        Pane pane = loader.load();
        NavigationController controller = loader.getController();
        controller.setStaffInformation(null);
        controller.setNavigation(this);
        return pane;
    }

    private Pane getAdministratorNavigationPane() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("navigationview/administratornavigation.fxml"));
        Pane pane = loader.load();
        NavigationController controller = loader.getController();
        controller.setStaffInformation(null);
        controller.setNavigation(this);
        return pane;
    }

    private void displayUserNavigationPane(){
        try {
            Node pane = null;
            AccountType accountType = LoginHandle.getInstance().getCurrentAccount().getAccountType();
            switch (accountType){
                case Receptionist: pane = getReceptionistNavigationPane();
                break;
                case Doctor: pane = getDoctorNavigationPane();
                break;
                case Administrator: pane = getAdministratorNavigationPane();
                break;
                case Cashier: pane = getCashierNavigationPane();
                break;
            }
            contentPane.setLeft(pane);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private final class DisplayScrollPane extends ScrollPane implements NodeDisplay {

        @Override public void display(Node item) {
            setContent(item);
            resetViewScroll();
        }
    }

}
