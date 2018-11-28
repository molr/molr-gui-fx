/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.molr.gui.fx.commands;

import org.molr.commons.domain.Mission;
import org.molr.commons.domain.MissionParameterDescription;

public class ViewMission {

    private final Mission mission;
    private final MissionParameterDescription description;


    public ViewMission(Mission mission, MissionParameterDescription description) {
        this.mission = mission;
        this.description = description;
    }

    public Mission mission() {
        return this.mission;
    }

    public MissionParameterDescription description() {
        return description;
    }
}
