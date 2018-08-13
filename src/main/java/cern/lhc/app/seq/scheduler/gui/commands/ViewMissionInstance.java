package cern.lhc.app.seq.scheduler.gui.commands;

import org.molr.commons.api.domain.MissionRepresentation;
import org.molr.commons.api.domain.MissionHandle;

public class ViewMissionInstance {

    private final MissionHandle missionHandle;
    private final MissionRepresentation missionRepresentation;

    public ViewMissionInstance(MissionHandle missionHandle, MissionRepresentation missionRepresentation) {
        this.missionHandle = missionHandle;
        this.missionRepresentation = missionRepresentation;
    }

    public MissionHandle missionHandle() {
        return this.missionHandle;
    }

    public MissionRepresentation missionDescription() {
        return this.missionRepresentation;
    }


}
