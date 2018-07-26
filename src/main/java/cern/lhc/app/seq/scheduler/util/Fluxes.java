/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.util;

import reactor.core.publisher.Flux;

public final class Fluxes {
    
    private Fluxes() {
        /* only static methods */
    }

    public static final <T, R> Flux<R> filterCast(Flux<T> inFlux, Class<R> classToCastTo) {
        return inFlux.filter(classToCastTo::isInstance).cast(classToCastTo);
    }
    
    
}
