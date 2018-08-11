package cern.lhc.app.seq.scheduler.gui.commands;

import org.molr.commons.api.domain.MissionDescription;
import org.molr.commons.api.domain.MissionHandle;

public class ViewMissionInstance {

    private final MissionHandle missionHandle;
    private final MissionDescription missionDescription;

    public ViewMissionInstance(MissionHandle missionHandle, MissionDescription missionDescription) {
        this.missionHandle = missionHandle;
        this.missionDescription = missionDescription;
    }

    public MissionHandle missionHandle() {
        return this.missionHandle;
    }

    public MissionDescription missionDescription() {
        return this.missionDescription;
    }


}
