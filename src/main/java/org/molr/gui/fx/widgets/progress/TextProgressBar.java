package org.molr.gui.fx.widgets.progress;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class TextProgressBar extends StackPane {

    private static final String RED_BAR = "red-bar";
    private static final String GREEN_BAR = "green-bar";
    private static final String ORANGE_BAR = "orange-bar";
    private static final String[] COLOR_STYLE_CLASSES = {RED_BAR, GREEN_BAR, ORANGE_BAR};

    private final ObjectProperty<Progress> progress = new SimpleObjectProperty<>(Progress.undefined());


    private final ProgressBar bar = new ProgressBar();
    private final Text text = new Text();
    private final static int DEFAULT_LABEL_PADDING = 5;

    TextProgressBar() {
        syncProgress();
        progress.addListener((observableValue, oldValue, newValue) -> syncProgress());
        bar.setMaxWidth(Double.MAX_VALUE);
        getStylesheets().add(getClass().getResource("progress.css").toExternalForm());
        getChildren().setAll(bar, text);
    }

    private void syncProgress() {
        Progress actualProgress = progress.get();

        if (actualProgress == null) {
            actualProgress = Progress.undefined();
        }

        text.setText(actualProgress.text());
        bar.setProgress(actualProgress.value());

        switch (actualProgress.result()) {
            case SUCCESS:
                setBarStyleClass(GREEN_BAR);
                break;
            case FAILED:
                setBarStyleClass(RED_BAR);
                break;
            default:
                setBarStyleClass(ORANGE_BAR);
                break;
        }

        bar.setMinHeight(text.getBoundsInLocal().getHeight() + DEFAULT_LABEL_PADDING * 2);
        bar.setMinWidth(text.getBoundsInLocal().getWidth() + DEFAULT_LABEL_PADDING * 2);
    }

    public ObjectProperty<Progress> progressProperty() {
        return this.progress;
    }

    public void setProgress(Progress progress) {
        this.progress.set(progress);
    }


    private void setBarStyleClass(String barStyleClass) {
        bar.getStyleClass().removeAll(COLOR_STYLE_CLASSES);
        bar.getStyleClass().add(barStyleClass);
    }
}
