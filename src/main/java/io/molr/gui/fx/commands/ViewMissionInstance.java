package io.molr.gui.fx.commands;

import io.molr.commons.domain.MissionInstance;
import io.molr.commons.domain.MissionParameterDescription;

public class ViewMissionInstance {

    private final MissionInstance missionHandle;
    private final MissionParameterDescription description;

    public ViewMissionInstance(MissionInstance missionHandle, MissionParameterDescription description) {
        this.missionHandle = missionHandle;
        this.description = description;
    }

    public MissionInstance missionInstance() {
        return this.missionHandle;
    }

    public MissionParameterDescription description() {
        return description;
    }
}
