/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.execution.events;

import static cern.lhc.app.seq.scheduler.domain.OnOff.OFF;
import static cern.lhc.app.seq.scheduler.domain.OnOff.ON;

import cern.lhc.app.seq.scheduler.domain.OnOff;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;

public class Cursor extends AbstractValueEvent<OnOff> {

    public Cursor(ExecutionBlock block, OnOff value) {
        super(block, value);
    }

    public static final Cursor on(ExecutionBlock block) {
        return new Cursor(block, ON);
    }

    public static final Cursor off(ExecutionBlock block) {
        return new Cursor(block, OFF);
    }

}
