/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.execution.demo;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

import cern.lhc.app.seq.scheduler.domain.attributes.Attribute;
import cern.lhc.app.seq.scheduler.domain.execution.AbstractExecutionBlock;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;

public class DemoBlock extends AbstractExecutionBlock {

    public DemoBlock(Builder builder) {
        super(builder.name, builder.children, builder.attributes);
    }

    public static final Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {
        private final String name;
        private List<ExecutionBlock> children = new ArrayList<>();
        private ClassToInstanceMap<Attribute<?, ?>> attributes = MutableClassToInstanceMap.create();

        Builder(String name) {
            this.name = requireNonNull(name, "name must not be null");
        }

        public Builder child(ExecutionBlock block) {
            this.children.add(block);
            return this;
        }

        public <A extends Attribute<?, A>> Builder attribute(A attribute) {
            this.attributes.put(attribute.markerClass(), attribute);
            return this;
        }

        public DemoBlock build() {
            return new DemoBlock(this);
        }
    }

    public static final DemoBlock ofName(String name) {
        return builder(name).build();
    }

}
