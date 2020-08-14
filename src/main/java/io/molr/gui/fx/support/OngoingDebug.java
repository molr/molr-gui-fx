package io.molr.gui.fx.support;

import com.google.common.collect.ImmutableMap;
import io.molr.commons.domain.Mission;
import io.molr.commons.domain.MissionHandle;
import io.molr.commons.domain.MissionParameterDescription;
import io.molr.gui.fx.widgets.MissionPane;
import io.molr.mole.core.api.Mole;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class OngoingDebug {

    private static final Logger LOGGER = LoggerFactory.getLogger(OngoingDebug.class);

    private final Mole mole;
    private final Mission mission;
    private final Supplier<Optional<Map<String, Object>>> params;

    public OngoingDebug(Mole mole, Mission mission, Supplier<Optional<Map<String, Object>>> params) {
        this.mole = requireNonNull(mole, "mole must not be null");
        this.mission = requireNonNull(mission, "mission must not be null");
        this.params = requireNonNull(params, "params must not be null");
    }

    public Optional<SimpleMissionControl> inNewStage() {
        Optional<Map<String, Object>> parameters = params.get();
        if (!parameters.isPresent()) {
            LOGGER.info("Aborted by user. Mission will not be instantiated.");
            return Optional.empty();
        }
        MissionHandle handle = instantiate(parameters.get());

        MissionPane pane = new MissionPane(mole, mission, handle);
        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        return Optional.of(new SimpleMissionControl(mole, handle));
    }

    private MissionHandle instantiate(Map<String, Object> pMap) {
        return mole.instantiate(mission, pMap).block();
    }
}
