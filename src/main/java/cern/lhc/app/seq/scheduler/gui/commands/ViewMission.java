/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.commands;

import org.molr.commons.api.domain.MissionRepresentation;

public class ViewMission {

    private final MissionRepresentation executable;

    public ViewMission(MissionRepresentation executable) {
        this.executable = executable;
    }

    public MissionRepresentation missionDescription() {
        return executable;
    }

}
