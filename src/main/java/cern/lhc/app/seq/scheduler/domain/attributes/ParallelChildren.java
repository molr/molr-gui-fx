/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.attributes;

public class ParallelChildren extends TrueAttribute<ParallelChildren> {

    @Override
    public Class<ParallelChildren> markerClass() {
        return ParallelChildren.class;
    }

}
