package userinterface;

import javafx.beans.property.ReadOnlyDoubleProperty;
import userinterface.navigation.Displayer;
import userinterface.navigation.UserNavigation;
import userinterface.navigation.UserNavigation.NavigationListener;
import userinterface.prompt.PromptSource;

public interface MainView {

    UserNavigation getUserNavigation();

    void addNavigationListener(NavigationListener listener);
    void removeNavigationListener(NavigationListener listener);

    ReadOnlyDoubleProperty getDisplayerWidthProperty();
    ReadOnlyDoubleProperty getDisplayerHeightProperty();


}
