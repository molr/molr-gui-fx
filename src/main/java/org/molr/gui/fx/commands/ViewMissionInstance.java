package org.molr.gui.fx.commands;

import org.molr.commons.domain.MissionInstance;
import org.molr.commons.domain.MissionParameterDescription;

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
