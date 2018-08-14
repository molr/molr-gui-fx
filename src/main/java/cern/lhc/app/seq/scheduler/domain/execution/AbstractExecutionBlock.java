/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.execution;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableList;

import cern.lhc.app.seq.scheduler.domain.attributes.Attribute;

public abstract class AbstractExecutionBlock implements ExecutionBlock {

    private final String name;
    private final List<ExecutionBlock> children;
    private final ClassToInstanceMap<? extends Attribute<?, ?>> attributes;

    public AbstractExecutionBlock(String name, List<? extends ExecutionBlock> children,
            ClassToInstanceMap<? extends Attribute<?, ?>> attributes) {
        this.name = requireNonNull(name, "name must not be null");
        requireNonNull(children, "childrenBlockIds must not be null");
        this.children = ImmutableList.copyOf(children);
        requireNonNull(attributes, "attributes must not be null");
        this.attributes = ImmutableClassToInstanceMap.copyOf(attributes);
    }

    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final List<ExecutionBlock> children() {
        return this.children;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T extends Attribute<?,?>> Optional<T> get(Class<T> attributeType) {
        return (Optional<T>) Optional.ofNullable(attributes.get(attributeType));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        result = prime * result + ((children == null) ? 0 : children.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractExecutionBlock other = (AbstractExecutionBlock) obj;
        if (attributes == null) {
            if (other.attributes != null) {
                return false;
            }
        } else if (!attributes.equals(other.attributes)) {
            return false;
        }
        if (children == null) {
            if (other.children != null) {
                return false;
            }
        } else if (!children.equals(other.children)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AbstractExecutionBlock [name=" + name + ", childrenBlockIds=" + children + ", attributes=" + attributes + "]";
    }

}
