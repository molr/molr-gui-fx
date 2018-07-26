/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.demo;

import static cern.lhc.app.seq.scheduler.domain.attributes.Attributes.PARALLEL_CHILDREN;
import static org.junit.Assert.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import cern.lhc.app.seq.scheduler.domain.attributes.Attributes;
import cern.lhc.app.seq.scheduler.domain.attributes.ParallelChildren;
import cern.lhc.app.seq.scheduler.domain.execution.demo.DemoBlock;

public class DemoBlockTest {

    @Test
    public void booleanAttributeWorks() {
        DemoBlock block = DemoBlock.builder("demo block").attribute(PARALLEL_CHILDREN).build();

        Optional<ParallelChildren> parallel = block.get(ParallelChildren.class);
        System.out.println(parallel.get().get());

        Assertions.assertThat(block.has(PARALLEL_CHILDREN)).isTrue();
    }

}
