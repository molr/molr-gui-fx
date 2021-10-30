package io.molr.gui.fx.widgets;

import java.util.Map;

import io.molr.commons.domain.Mission;
import io.molr.commons.domain.MissionHandle;
import io.molr.commons.domain.MissionParameterDescription;
import io.molr.gui.fx.FxThreadScheduler;
import io.molr.gui.fx.util.FormattedButton;
import io.molr.mole.core.api.Mole;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

public class MissionInstantiationPane extends BorderPane{
	
	VBox elements = new VBox();
	
	Mole mole;
	Mission mission;
		
	MonoProcessor<MissionHandle> handleMonoProcessor = MonoProcessor.create();
	
	MissionInstantiationPane(Mole mole, Mission mission){
		this.mole = mole;
		this.mission = mission;
		configureInstantiable();
	}

	private void configureInstantiable() {
		MissionParameterDescription description = mole.parameterDescriptionOf(mission).block();
		ParameterEditor parameterEditor = new ParameterEditor(description.parameters());
		elements.getChildren().add(parameterEditor);

		FormattedButton instantiateButton = new FormattedButton("Instantiate", "Instantiate", "Blue");
		instantiateButton.getButton().setOnAction(event -> {
			instantiateButton.getButton().setDisable(true);
			this.instantiate(parameterEditor.parameterValues());
		});
		elements.getChildren().add(instantiateButton.getButton());
		setCenter(elements);
	}

	private void instantiate(Map<String, Object> params) {
		mole.instantiate(mission, params).publishOn(FxThreadScheduler.instance()).subscribe(h -> {
			handleMonoProcessor.onNext(h);
		});
	}
	
	Mono<MissionHandle> missionHandle(){
		return handleMonoProcessor;
	}

}
