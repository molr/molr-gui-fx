package org.molr.gui.fx.widgets;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.molr.gui.fx.util.Jsons;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.function.Consumer;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;

public class JsonStreamDebuggingTab extends Tab {

    private final Object monitor = new Object();

    private static final String DEFAULT_TEXT = "http://";
    private final TextArea textArea;
    private final TextField inputField;

    private Disposable activeSubscription = null;

    private final WebClient client = WebClient.create();

    public JsonStreamDebuggingTab() {
        this(DEFAULT_TEXT);
    }

    public JsonStreamDebuggingTab(String uri) {
        super(uri);
        this.textArea = new TextArea();

        BorderPane content = new BorderPane();
        content.setCenter(textArea);

        inputField = new TextField();
        inputField.setText(uri);
        inputField.setOnAction(event -> subscribe());
        content.setTop(inputField);

        setContent(content);

        setOnClosed(evt -> unsubscribe());
    }

    public void subscribe() {
        String u = inputField.getText();
        super.setText(u);
        debugStream(u);
    }

    private void debugStream(String uri) {
        Mono<ClientResponse> streamData = client.get()
                .uri(uri)
                .accept(APPLICATION_STREAM_JSON)
                .exchange();
        streamData.subscribe(this::subscribeTo, this::appendThrowable);
    }

    private void unsubscribe() {
        synchronized (monitor) {
            if (activeSubscription != null) {
                activeSubscription.dispose();
            }
        }
    }

    private void subscribeTo(ClientResponse res) {
        synchronized (monitor) {
            unsubscribe();
            Platform.runLater(() -> textArea.clear());
            Consumer<String> appendLine = this::appendLine;
            Consumer<Throwable> appendThrowable = this::appendThrowable;
            activeSubscription = res.bodyToFlux(String.class).subscribe(appendLine, appendThrowable);
        }
    }

    private void appendThrowable(Throwable t) {
        String stacktrace = Arrays.stream(t.getStackTrace()).map(Object::toString).collect(joining("\n"));
        appendLine(stacktrace);
    }

    private void appendLine(String v) {
        String indented = Jsons.prettyPring(v);
        Platform.runLater(() -> textArea.appendText(indented + "\n-----\n"));
    }




}
