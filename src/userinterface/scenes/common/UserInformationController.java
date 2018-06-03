package userinterface.scenes.common;

import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import userinterface.scenes.BaseController;

import java.net.URL;
import java.util.ResourceBundle;

public class UserInformationController extends BaseController {

    @FXML Pane container;

    @FXML Pane profileViewContainer;

    @FXML Button profileEditButton;

    @FXML Label staffNumberLabel;
    @FXML Label nameLabel;
    @FXML Label genderLabel;
    @FXML Label ageLabel;
    @FXML Label birthdateLabel;
    @FXML Label positionLabel;
    @FXML Label expertiseLabel;
    @FXML Label contactNumberLabel;
    @FXML Label emailLabel;
    @FXML Label addressLabel;


    @FXML Pane profileEditForm;



    @FXML Pane accountViewContainer;

    @FXML Button changePasswordButton;
    @FXML Button editAccountButton;


    @FXML Pane accountEditForm;

    @FXML Pane passwordEditForm;


    final SimpleBooleanProperty isProfileEditing = new SimpleBooleanProperty(false);
    final SimpleBooleanProperty isAccountEditing = new SimpleBooleanProperty(false);
    final SimpleBooleanProperty isPasswordEditing = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        super.initialize(location, bundle);
        ReadOnlyDoubleProperty prop = ((Pane)container.getParent()).widthProperty();
        container.prefWidthProperty().bind(prop);
        initializeViewComponents();
    }

    private void initializeViewComponents() {
        final BooleanBinding notEditing = isProfileEditing.not();
        profileViewContainer.visibleProperty().bind(notEditing);
        profileViewContainer.managedProperty().bind(profileViewContainer.visibleProperty());
        profileEditForm.visibleProperty().bind(isProfileEditing);
        profileEditForm.managedProperty().bind(profileEditForm.visibleProperty());
        profileEditButton.visibleProperty().bind(notEditing);


        editAccountButton.visibleProperty().bind(isAccountEditing);
        accountViewContainer.visibleProperty().bind(isAccountEditing.not());
        accountViewContainer.managedProperty().bind(accountViewContainer.visibleProperty());
        accountEditForm.visibleProperty().bind(isAccountEditing);
        accountEditForm.managedProperty().bind(accountEditForm.visibleProperty());

        passwordEditForm.visibleProperty().bind(isPasswordEditing);
        passwordEditForm.managedProperty().bind(passwordEditForm.visibleProperty());
        changePasswordButton.visibleProperty().bind(isPasswordEditing.not());
        changePasswordButton.managedProperty().bind(changePasswordButton.visibleProperty());
    }

    @FXML private void profileEditAction(ActionEvent event){
        isProfileEditing.setValue(true);
    }

    @FXML private void profileCancelEditAction(ActionEvent event){
        isProfileEditing.setValue(false);
    }

    @FXML private void profileApplyEditAction(ActionEvent event){

    }

    @FXML private void accountEditAction(ActionEvent event){
        isAccountEditing.setValue(true);
    }

    @FXML private void accountCancelEditAction(ActionEvent event){
        isAccountEditing.setValue(false);
    }

    @FXML private void accountApplyEditAction(ActionEvent event){

    }

    @FXML private void passwordChangeAction(ActionEvent event){
        isPasswordEditing.setValue(true);
    }

    @FXML private void applyPasswordChangeAction(ActionEvent event){

    }

    @FXML private void cancelPasswordChangeAction(ActionEvent event){
        isPasswordEditing.setValue(false);
    }







}
