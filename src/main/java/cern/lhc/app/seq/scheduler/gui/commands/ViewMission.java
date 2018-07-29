/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.commands;

import cern.lhc.app.seq.scheduler.domain.molr.MissionDescription;

public class ViewMission {

    private final MissionDescription executable;

    public ViewMission(MissionDescription executable) {
        this.executable = executable;
    }

    public MissionDescription missionDescription() {
        return executable;
    }

}
