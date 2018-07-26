/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.execution.adapt;

import static cern.lhc.app.seq.scheduler.util.Fluxes.filterCast;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionEvent;
import cern.lhc.app.seq.scheduler.domain.execution.events.AbstractValueEvent;
import reactor.core.publisher.Flux;

public class EventSummarizer<T, E extends AbstractValueEvent<T>> {

    private final Map<ExecutionBlock, T> childResults = new ConcurrentHashMap<>();
    private final Flux<E> summaryFlux;

    public EventSummarizer(Iterable<ExecutionBlock> childBlocks, T initialValue, Class<E> eventType,
            Function<Iterable<T>, E> eventSummarizer, Flux<? extends ExecutionEvent> inFlux) {
        for (ExecutionBlock block : childBlocks) {
            this.childResults.put(block, initialValue);
        }
        this.summaryFlux = filterCast(inFlux, eventType).filter(r -> childResults.containsKey(r.block())).map(rc -> {
            this.childResults.put(rc.block(), rc.value());
            return eventSummarizer.apply(childResults.values());
        });
    }

    public Flux<E> asFlux() {
        return this.summaryFlux;
    }

}
