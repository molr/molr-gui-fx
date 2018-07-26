/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.attributes;

public class ActiveBreakpoint extends TrueAttribute<ActiveBreakpoint> {

    @Override
    public Class<ActiveBreakpoint> markerClass() {
        return ActiveBreakpoint.class;
    }

}
