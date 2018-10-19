/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.execution.adapt;

import static org.molr.commons.domain.RunState.FINISHED;
import static org.molr.commons.domain.RunState.RUNNING;
import static cern.lhc.app.seq.scheduler.domain.attributes.Attributes.PARALLEL_CHILDREN;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.molr.commons.domain.Result;
import org.molr.commons.domain.RunState;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionEvent;
import cern.lhc.app.seq.scheduler.domain.execution.events.Cursor;
import cern.lhc.app.seq.scheduler.domain.execution.events.ResultChange;
import cern.lhc.app.seq.scheduler.domain.execution.events.RunStateChange;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public abstract class SingleBlockAdapter {

    private final AtomicBoolean executing = new AtomicBoolean(false);
    private final EmitterProcessor<ExecutionEvent> events = EmitterProcessor.create();
    private final Flux<ExecutionEvent> fullFlux;
    private final Map<ExecutionBlock, SingleBlockAdapter> childrenAdapters;

    protected final ExecutionBlock block;
    protected final FluxSink<ExecutionEvent> eventSink = events.sink();
    protected final ExecutionAdapter delegate;

    public SingleBlockAdapter(ExecutionAdapter delegate, ExecutionBlock block) {
        this.delegate = requireNonNull(delegate, "delegate must not be null");
        this.block = requireNonNull(block, "block must not be null");

        this.childrenAdapters = childrenAdapters();
        this.fullFlux = fullFlux();
    }

    private Map<ExecutionBlock, SingleBlockAdapter> childrenAdapters() {
        return block.children().stream().collect(toImmutableMap(identity(), this::newAdapter));
    }

    public final void execute() {
        if (executing.getAndSet(true)) {
            throw new IllegalStateException("Already executing! Not allowed to call twice.");
        }

        eventSink.next(Cursor.on(this.block));
        eventSink.next(RunStateChange.of(this.block, RUNNING));

        this.doWork();

        eventSink.next(RunStateChange.of(this.block, FINISHED));
        eventSink.next(Cursor.off(this.block));
    }

    protected abstract void doWork();

    private Flux<ExecutionEvent> fullFlux() {
        Flux<ExecutionEvent> childrenFlux = Flux.merge(childrenFluxes());

        Flux<ResultChange> resultChanges = new EventSummarizer<>(block.children(), Result.UNDEFINED, ResultChange.class,
                rs -> ResultChange.summaryFor(this.block, rs), childrenFlux).asFlux();

        Flux<RunStateChange> runStateChanges = new EventSummarizer<>(block.children(), RunState.UNDEFINED,
                RunStateChange.class, rs -> RunStateChange.summaryFor(this.block, rs), childrenFlux).asFlux();

        return Flux.merge(events, childrenFlux, resultChanges, runStateChanges).share();
    }

    private List<Flux<ExecutionEvent>> childrenFluxes() {
        return this.block.children().stream().map(this::childFlux).collect(toList());
    }

    protected final void executeChild(ExecutionBlock child) {
        this.childrenAdapters.get(child).execute();
    }

    private Flux<ExecutionEvent> childFlux(ExecutionBlock child) {
        return this.childrenAdapters.get(child).events();
    }

    private SingleBlockAdapter newAdapter(ExecutionBlock child) {
        return newAdapter(child, delegate);
    }

    public static final SingleBlockAdapter newAdapter(ExecutionBlock child, ExecutionAdapter leafAdapter) {
        if (child.children().isEmpty()) {
            return new LeafBlockAdapter(leafAdapter, child);
        }
        if (child.has(PARALLEL_CHILDREN)) {
            return new ParallelBlockAdapter(leafAdapter, child);
        }
        return new SequentialBlockAdapter(leafAdapter, child);
    }

    public Flux<ExecutionEvent> events() {
        return fullFlux;
    }

}
