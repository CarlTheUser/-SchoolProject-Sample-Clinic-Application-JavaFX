package core.model;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public abstract class Model implements Validateable {

    private SaveListener saveListener;
    private EditListener editListener;
    private ErrorListener errorListener;
    private ValidationListener validationListener;

    private final ReadOnlyBooleanWrapper isEditing = new ReadOnlyBooleanWrapper(this, "isEditing", false);
//    private final ReadOnlyBooleanWrapper isDirty = new ReadOnlyBooleanWrapper(this, "isDirty", false);

    protected abstract void backupFields();
    protected abstract void restoreFields();
    protected abstract void clearBackup();
    protected abstract void saveMethod() throws Exception;
    protected abstract void updateMethod() throws Exception;
    protected abstract void deleteMethod() throws Exception;
    protected abstract void refreshMethod() throws Exception;

    protected abstract boolean isIdentifiable();

    protected abstract AbstractList<ValidationCriterion> createValidationCriteria();

//    protected void setDirty(boolean isDirty) { this.isDirty.set(isDirty); }

    private boolean restoreBackupOnInvalid = true;

    protected boolean isNew = false;

    @Override public boolean isValid() {
        final AbstractList<ValidationCriterion> validationCriteria = createValidationCriteria();
        if(validationCriteria == null) return true;
        final List<String> brokenRules = new ArrayList<>();
        for(ValidationCriterion v : validationCriteria){
            if(!v.validate()) brokenRules.add(v.getErrorMessage());
        }
        if(validationListener != null) validationListener.onValidated(new AbstractList<String>() {
            @Override
            public String get(int index) {
                return brokenRules.get(index);
            }

            @Override
            public int size() {
                return brokenRules.size();
            }
        });
        boolean isValid = ((brokenRules != null) ? brokenRules.size() == 0 : true);
        if(!isValid && restoreBackupOnInvalid) restoreFields();
        return isValid;
    }

    public void save(){
        if(isValid()){
            try{
                saveMethod();
                isNew = false;
//                isDirty.set(false);
                if(saveListener != null) saveListener.onSaved();
            }catch (Exception e){
                e.printStackTrace();
                if(errorListener != null) errorListener.onError(e.getMessage());
            }
        }
    }

    public void beginEdit(){
        backupFields();
        isEditing.set(true);
        if(editListener != null) editListener.onEditBegun();
    }

    public void cancelEdit(){
        isEditing.set(false);
        restoreFields();
//        isDirty.set(false);
        if(editListener != null) editListener.onEditCancelled();
    }

    public void applyEdit(){
        if(isValid()) {
            try {
                if(!isNew) updateMethod();
                isEditing.set(false);
//                isDirty.set(false);
                if(editListener != null) editListener.onEditApplied();
            }catch (Exception e){
                if(errorListener != null) errorListener.onError(e.getMessage());
            }
        }
    }

    public void refresh(){
        try {
            refreshMethod();
        } catch (Exception e) {
            if(errorListener != null) errorListener.onError(e.getMessage());
        }
        isEditing.set(false);
//        isDirty.set(false);
    }

//    protected final ChangeListener propertyChangeListener = new ChangeListener() {
//        @Override
//        public void stateChanged(ChangeEvent e) {
//            isDirty.set(true);
//        }
//    };


    public boolean isNew() {
        return isNew;
    }

    public boolean isRestoreBackupOnInvalid() {
        return restoreBackupOnInvalid;
    }

    public void setRestoreBackupOnInvalid(boolean restoreBackupOnInvalid) {
        this.restoreBackupOnInvalid = restoreBackupOnInvalid;
    }

    public boolean isEditing() {
        return isEditing.get();
    }

    public ReadOnlyBooleanProperty isEditingProperty() {
        return isEditing.getReadOnlyProperty();
    }

//    public boolean isDirty() {
//        return isDirty.get();
//    }
//
//    public ReadOnlyBooleanWrapper isDirtyProperty() {
//        return isDirty;
//    }

    public SaveListener getSaveListener() {
        return saveListener;
    }

    public void setSaveListener(SaveListener saveListener) {
        this.saveListener = saveListener;
    }

    public EditListener getEditListener() {
        return editListener;
    }

    public void setEditListener(EditListener editListener) {
        this.editListener = editListener;
    }

    public ErrorListener getErrorListener() {
        return errorListener;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public ValidationListener getValidationListener() {
        return validationListener;
    }

    public void setValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    public interface EditListener{
        void onEditBegun();
        void onEditCancelled();
        void onEditApplied();
    }

    public interface SaveListener{
        void onSaved();
    }

    public interface ErrorListener{
        void onError(String message);
    }



}
