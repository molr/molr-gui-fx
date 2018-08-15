package cern.lhc.app.seq.scheduler.gui.commands;

import org.molr.commons.api.domain.MissionInstance;
import org.molr.commons.api.domain.MissionRepresentation;
import org.molr.commons.api.domain.MissionHandle;

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
