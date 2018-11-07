package cern.lhc.app.seq.scheduler.gui.commands;

import org.molr.commons.domain.MissionInstance;
import org.molr.commons.domain.MissionParameterDescription;
import org.molr.commons.domain.MissionRepresentation;

public class ViewMissionInstance {

    private final MissionInstance missionHandle;
    private final MissionRepresentation missionRepresentation;
    private final MissionParameterDescription description;

    public ViewMissionInstance(MissionInstance missionHandle, MissionRepresentation missionRepresentation, MissionParameterDescription description) {
        this.missionHandle = missionHandle;
        this.missionRepresentation = missionRepresentation;
        this.description = description;
    }

    public MissionInstance missionInstance() {
        return this.missionHandle;
    }

    public MissionRepresentation missionDescription() {
        return this.missionRepresentation;
    }


    public MissionParameterDescription description() {
        return description;
    }
}
