/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.execution.adapt;

import static cern.lhc.app.seq.scheduler.domain.attributes.Attributes.PARALLEL_CHILDREN;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;
import cern.lhc.app.seq.scheduler.domain.execution.demo.DemoBlock;

public class BlockExecutorTest {

    private ExecutionAdapter delegate;

    private DemoBlock childA;
    private DemoBlock childB;
    private DemoBlock childC;
    private DemoBlock childD;

    @Before
    public void setUp() {
        this.delegate = Mockito.mock(ExecutionAdapter.class);

        this.childA = DemoBlock.ofName("child A");
        this.childB = DemoBlock.ofName("child B");
        this.childC = DemoBlock.ofName("child C");
        this.childD = DemoBlock.ofName("child D");

    }

    @Test
    public void leafIsDirectlyExecuted() {
        verifyExecution(childA, childA);
    }

    @Test
    public void parallelChildrenAreExecutedSeparately() {
        // @formatter:off
        DemoBlock block = DemoBlock.builder("block a")
                .child(childA)
                .child(childB)
                .attribute(PARALLEL_CHILDREN)
                .build();
        // @formatter:on

        verifyExecution(block, childA, childB);
    }

    @Test
    public void parallelBelowSerialWorks() {

        // @formatter:off
        DemoBlock parallel = DemoBlock.builder("parallel child")
                .child(childC)
                .child(childD)
                .attribute(PARALLEL_CHILDREN)
                .build();

        DemoBlock root = DemoBlock.builder("serial root")
                .child(childA)
                .child(parallel)
                .child(childB)
                .build();
        // @formatter:on

        verifyExecution(root, childA, childC, childD, childB);

    }

    private void verifyExecution(ExecutionBlock root, ExecutionBlock... calls) {
        SingleBlockAdapter.newAdapter(root, delegate).execute();

        for (ExecutionBlock block : calls) {
            verify(delegate, times(1)).execute(block);
        }
    }

}
