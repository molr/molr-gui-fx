/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.commands;

import cern.lhc.app.seq.scheduler.domain.molr.MissionDescription;

public class Open {

    private final MissionDescription executable;

    public Open(MissionDescription executable) {
        this.executable = executable;
    }

    public MissionDescription executable() {
        return executable;
    }

}
