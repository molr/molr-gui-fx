package io.molr.gui.fx.support;

import io.molr.commons.domain.Mission;
import io.molr.commons.domain.MissionParameterDescription;
import io.molr.gui.fx.widgets.MolrDialogs;
import io.molr.mole.core.api.Mole;
import javafx.scene.control.Dialog;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

public class MolrFxSupportImpl implements MolrFxSupport {

    private final Mole mole;

    public MolrFxSupportImpl(Mole mole) {
        this.mole = requireNonNull(mole, "mole must not be null");
    }

    @Override
    public OngoingDebug debug(Mission mission) {
        return new OngoingDebug(mole, mission, () -> parametersFor(mission));
    }

    @Override
    public OngoingDebug debug(Mission mission, Map<String, Object> parameters) {
        return new OngoingDebug(mole, mission, () -> Optional.of(parameters));
    }

    @Override
    public Set<Mission> executableMissions() {
        return mole.states().blockFirst().executableMissions();
    }

    private Optional<Map<String, Object>> parametersFor(Mission mission) {
        MissionParameterDescription description = mole.parameterDescriptionOf(mission).block();

        if (description.parameters().isEmpty()) {
            /* This is a valid situation! There are no parameters, so no need to query them ;-)*/
            return Optional.of(emptyMap());
        }

        Dialog<Map<String, Object>> dialog = MolrDialogs.parameterDialogFor(mission, description);
        return dialog.showAndWait();
    }
}
