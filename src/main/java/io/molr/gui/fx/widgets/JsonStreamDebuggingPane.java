package io.molr.gui.fx.widgets;

import io.molr.gui.fx.perspectives.DebugPerspective;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.minifx.workbench.annotations.View;
import org.springframework.stereotype.Component;

import static org.minifx.fxcommons.util.Fillers.horizontalFiller;
import static org.minifx.workbench.domain.PerspectivePos.CENTER;

@Component
@View(in = DebugPerspective.class, at = CENTER)
public class JsonStreamDebuggingPane extends BorderPane {

    private static final String DEFAULT_BASE = "http://localhost:8000";
    private final static String[] DEFAULT_PATHS = new String[]{"/test-stream/5",
            "/states",
            "/mission/Executable Leafs Demo Mission/instantiate",
            "/mission/Executable Leafs Demo Mission/parameter-description",
            "/mission/Executable Leafs Demo Mission/representation",
            "/mission/Linear Mission/instantiate",
            "/mission/Linear Mission/parameter-description",
            "/mission/Linear Mission/representation",
            "/mission/Conquer Rome/representation",
            "/instance/0/states",
            "/instance/0/0/instruct/PAUSE",
            "/instance/0/0/instruct/RESUME"
    };

    private final TabPane tabPane;

    public JsonStreamDebuggingPane() {
        this.tabPane = new TabPane();
        setCenter(tabPane);

        HBox buttonPane = new HBox();
        Button newButton = new Button("+");
        newButton.setOnAction(event -> {
            JsonStreamDebuggingTab tab = new JsonStreamDebuggingTab();
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
        });

        TextField base = new TextField(DEFAULT_BASE);
        base.setPrefWidth(150);

        ComboBox<String> paths = new ComboBox<>();
        paths.getItems().addAll(DEFAULT_PATHS);
        paths.getSelectionModel().select(0);

        Button newFromTemplateButton = new Button("create");
        newFromTemplateButton.setOnAction(event -> {
            String uri = base.getText() + paths.getSelectionModel().getSelectedItem();
            JsonStreamDebuggingTab tab = new JsonStreamDebuggingTab(uri);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
            tab.subscribe();
        });

        buttonPane.setSpacing(10);
        buttonPane.setAlignment(Pos.CENTER_LEFT);
        buttonPane.getChildren().addAll(newButton, horizontalFiller(), base, paths, newFromTemplateButton);
        setTop(buttonPane);
    }

}
