/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.attributes;

public abstract class TrueAttribute<A extends Attribute<Boolean, A>> implements Attribute<Boolean, A> {

    @Override
    public final Boolean get() {
        return true;
    }

}