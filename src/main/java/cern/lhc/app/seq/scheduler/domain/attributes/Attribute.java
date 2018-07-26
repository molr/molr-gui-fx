/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.attributes;

public interface Attribute<T, C extends Attribute<T, C>> {

    public T get();

    public Class<C> markerClass();

}
