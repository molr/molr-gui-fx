package cern.lhc.app.seq.scheduler.gui.commands;

import org.molr.commons.domain.MissionInstance;
import org.molr.commons.domain.MissionRepresentation;

public class ViewMissionInstance {

    private final MissionInstance missionHandle;
    private final MissionRepresentation missionRepresentation;

    public ViewMissionInstance(MissionInstance missionHandle, MissionRepresentation missionRepresentation) {
        this.missionHandle = missionHandle;
        this.missionRepresentation = missionRepresentation;
    }

    public MissionInstance missionInstance() {
        return this.missionHandle;
    }

    public MissionRepresentation missionDescription() {
        return this.missionRepresentation;
    }


}
