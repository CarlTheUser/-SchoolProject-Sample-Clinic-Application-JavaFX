package core.model;

import java.util.AbstractList;

public interface Validateable {
    boolean isValid();

    abstract class ValidationCriterion {
        private final String errorMessage;
        public abstract boolean validate();
        public ValidationCriterion(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        public String getErrorMessage() {
            return errorMessage;
        }
    }
    interface ValidationListener{
        void onValidated(AbstractList<String> brokenRules);
    }
}
