/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package io.molr.gui.fx.commands;

import io.molr.commons.domain.Mission;
import io.molr.commons.domain.MissionParameterDescription;

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
