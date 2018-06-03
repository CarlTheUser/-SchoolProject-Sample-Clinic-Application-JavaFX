package userinterface.prompt;

import javafx.stage.Stage;

public abstract class Prompt<T> {

    protected final Stage parent;

    public Prompt(Stage parent) {
        this.parent = parent;
    }

    public abstract T getResponse(String promptTitle, String promptBody);
}
