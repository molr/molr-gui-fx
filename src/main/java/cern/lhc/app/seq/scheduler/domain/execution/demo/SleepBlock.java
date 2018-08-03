/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.execution.demo;

import cern.lhc.app.seq.scheduler.domain.attributes.Attribute;
import cern.lhc.app.seq.scheduler.domain.execution.AbstractExecutionBlock;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class SleepBlock extends AbstractExecutionBlock {

    private final Duration duration;

    public SleepBlock(Builder builder) {
        super(builder.name, builder.children, builder.attributes);
        this.duration = builder.duration;
    }

    public static final Builder builder(String name, long duration, TemporalUnit unit) {
        return new Builder(name, Duration.of(duration, unit));
    }

    public Duration duration() {
        return this.duration;
    }

    public static class Builder {
        private final String name;
        private final Duration duration;
        private List<ExecutionBlock> children = new ArrayList<>();
        private ClassToInstanceMap<Attribute<?, ?>> attributes = MutableClassToInstanceMap.create();

        Builder(String name, Duration duration) {
            this.name = requireNonNull(name, "name must not be null");
            this.duration = requireNonNull(duration, "duration must not be null");
        }

        public Builder child(ExecutionBlock block) {
            this.children.add(block);
            return this;
        }

        public <A extends Attribute<?, A>> Builder attribute(A attribute) {
            this.attributes.put(attribute.markerClass(), attribute);
            return this;
        }

        public SleepBlock build() {
            return new SleepBlock(this);
        }
    }

    public static final SleepBlock of(String name, long duration, TemporalUnit unit) {
        return builder(name, duration, unit).build();
    }

}
