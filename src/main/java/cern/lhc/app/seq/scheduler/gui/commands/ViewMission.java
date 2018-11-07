/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.commands;

import org.molr.commons.domain.Mission;
import org.molr.commons.domain.MissionParameterDescription;
import org.molr.commons.domain.MissionRepresentation;

public class ViewMission {

    private final Mission mission;
    private final MissionRepresentation representation;
    private final MissionParameterDescription description;


    public ViewMission(Mission mission, MissionRepresentation executable, MissionParameterDescription description) {
        this.mission = mission;
        this.representation = executable;
        this.description = description;
    }

    public Mission mission() {
        return this.mission;
    }

    public MissionRepresentation representation() {
        return representation;
    }

    public MissionParameterDescription description() {
        return description;
    }
}
