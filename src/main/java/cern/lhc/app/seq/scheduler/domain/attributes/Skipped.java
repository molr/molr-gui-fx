/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.attributes;

public class Skipped extends TrueAttribute<Skipped> {

    @Override
    public Class<Skipped> markerClass() {
        return Skipped.class;
    }

}
