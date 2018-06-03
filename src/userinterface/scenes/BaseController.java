package userinterface.scenes;

import javafx.fxml.Initializable;
import userinterface.Main;
import userinterface.MainView;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class BaseController implements Initializable {

    protected MainView getMainView(){
        return Main.getActiveInstance().getMainView();
    }

    protected Map<String, Object> parameters;

    private boolean parameterSet = false;

    @Override public void initialize(URL location, ResourceBundle bundle){

    }

    public void setParameters(Map<String, Object> parameters){
        if(!parameterSet) {
            this.parameters = parameters;
            parameterSet = true;
            System.out.println("BaseController firing onParametersReceived line 29");
            onParametersReceived();
            System.out.println("BaseController fired onParametersReceived line 31");
        }
    }

    protected void onParametersReceived(){
        System.out.println("BaseController handling onParametersReceived line 36");
    }

    public void dispose(){
        parameters = null;
    }

}
