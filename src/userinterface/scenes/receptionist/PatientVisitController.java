package userinterface.scenes.receptionist;

import core.model.VisitRecord;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import userinterface.scenes.BaseController;

import java.net.URL;
import java.util.ResourceBundle;

public class PatientVisitController extends BaseController {

    public static final String PATIENT_PARAMETER_NAME = "patient";

    public static final String VISITRECORD_PARAMETER_NAME = "visit_record";

    @FXML Pane container;



    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        super.initialize(location, bundle);
        ReadOnlyDoubleProperty property = getMainView().getDisplayerWidthProperty();
        container.prefWidthProperty().bind(property);
    }

    @Override
    protected void onParametersReceived() {
        super.onParametersReceived();
//        model.setVisitRecord((VisitRecord)parameters.get(VISITRECORD_PARAMETER_NAME));
    }
}
