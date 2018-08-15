/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.commands;

import org.molr.commons.api.domain.Mission;
import org.molr.commons.api.domain.MissionRepresentation;

public class ViewMission {

    private final Mission mission;
    private final MissionRepresentation executable;

    public ViewMission(Mission mission, MissionRepresentation executable) {
        this.mission = mission;
        this.executable = executable;
    }

    public Mission mission() {
        return this.mission;
    }

    public MissionRepresentation missionDescription() {
        return executable;
    }

}
