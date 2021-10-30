package io.molr.gui.fx.widgets;

import io.molr.commons.domain.MissionHandle;
import io.molr.commons.domain.MissionOutput;
import io.molr.gui.fx.FxThreadScheduler;
import io.molr.mole.core.api.Mole;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import reactor.core.publisher.Flux;

public class OutputPane extends TitledPane{
	
	TextArea textArea = new TextArea();
	
	public OutputPane(Mole mole, MissionHandle handle) {
		super();
		this.setText("Output");
		this.setContent(textArea);
        Flux<MissionOutput> missionOutputsFlux = mole.outputsFor(handle).publishOn(FxThreadScheduler.instance());
        missionOutputsFlux.subscribe(output -> {
        	this.textArea.setText(output.pretty());
        }, error -> {
        }, ()->{
        	
        });
	}
}
