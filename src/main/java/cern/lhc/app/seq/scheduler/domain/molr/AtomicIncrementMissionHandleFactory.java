/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.molr;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicIncrementMissionHandleFactory implements MissionHandleFactory {

    private final AtomicLong nextId = new AtomicLong(0);

    @Override
    public MissionHandle next() {
        return MissionHandle.ofMissionAndId(nextId.getAndIncrement());
    }

}
