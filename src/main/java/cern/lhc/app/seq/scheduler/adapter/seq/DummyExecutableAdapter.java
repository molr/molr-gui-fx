/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.adapter.seq;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cern.lhc.app.seq.scheduler.domain.Result;
import cern.lhc.app.seq.scheduler.domain.RunState;
import cern.lhc.app.seq.scheduler.domain.execution.demo.DemoBlock;
import cern.lhc.app.seq.scheduler.gui.commands.Open;
import cern.lhc.app.seq.scheduler.gui.commands.ResultChange;
import cern.lhc.app.seq.scheduler.gui.commands.RunStateChange;

public class DummyExecutableAdapter extends AbstractExecutableAdapter implements ExecutableAdapter {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private DemoBlock root;
    private DemoBlock ss2;
    private DemoBlock l1a;
    private DemoBlock l1b;

    public void init() {
        dummyTree();

        executor.schedule(() -> opens.onNext(new Open(root)), 5, TimeUnit.SECONDS);
        executor.schedule(() -> runStateChanges.onNext(new RunStateChange(l1a, RunState.RUNNING)), 7, TimeUnit.SECONDS);
        executor.schedule(() -> runStateChanges.onNext(new RunStateChange(l1b, RunState.RUNNING)), 9, TimeUnit.SECONDS);

        executor.schedule(() -> runStateChanges.onNext(new RunStateChange(ss2, RunState.RUNNING)), 15,
                TimeUnit.SECONDS);

        executor.schedule(() -> runStateChanges.onNext(new RunStateChange(l1a, RunState.FINISHED)), 22,
                TimeUnit.SECONDS);
        executor.schedule(() -> runStateChanges.onNext(new RunStateChange(l1b, RunState.FINISHED)), 24,
                TimeUnit.SECONDS);

        executor.schedule(() -> runStateChanges.onNext(new RunStateChange(ss2, RunState.FINISHED)), 25,
                TimeUnit.SECONDS);

        executor.schedule(() -> resultChanges.onNext(new ResultChange(l1a, Result.SUCCESS)), 22, TimeUnit.SECONDS);
        executor.schedule(() -> resultChanges.onNext(new ResultChange(l1b, Result.FAILED)), 24, TimeUnit.SECONDS);

        executor.schedule(() -> resultChanges.onNext(new ResultChange(ss2, Result.SUCCESS)), 25, TimeUnit.SECONDS);

    }

    private DemoBlock dummyTree() {
        l1a = DemoBlock.ofName("Leaf 1A");
        l1b = DemoBlock.ofName("Leaf 1B");
        DemoBlock ss1 = DemoBlock.builder("subSeq 1").child(l1a).child(l1b).build();
        ss2 = DemoBlock.builder("subSeq 2").child(DemoBlock.ofName("Leaf 2A")).child(DemoBlock.ofName("Leaf 2B"))
                .build();
        root = DemoBlock.builder("Main Seq").child(ss1).child(ss2).build();
        return root;
    }

}
