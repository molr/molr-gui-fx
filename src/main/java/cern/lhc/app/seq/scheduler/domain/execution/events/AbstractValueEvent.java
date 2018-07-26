/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.execution.events;

import static java.util.Objects.requireNonNull;

import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionEvent;

public abstract class AbstractValueEvent<V> implements ExecutionEvent {

    private final ExecutionBlock block;
    private final V value;

    public AbstractValueEvent(ExecutionBlock block, V value) {
        this.block = requireNonNull(block, "block must not be null");
        this.value = requireNonNull(value, "value must not be null");
    }

    @Override
    public ExecutionBlock block() {
        return block;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((block == null) ? 0 : block.hashCode());
        result = prime * result + ((value() == null) ? 0 : value().hashCode());
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
        AbstractValueEvent<?> other = (AbstractValueEvent<?>) obj;
        if (block == null) {
            if (other.block != null) {
                return false;
            }
        } else if (!block.equals(other.block)) {
            return false;
        }
        if (value() == null) {
            if (other.value() != null) {
                return false;
            }
        } else if (!value().equals(other.value())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [block=" + block + ", value=" + value() + "]";
    }

    public V value() {
        return value;
    }

}
