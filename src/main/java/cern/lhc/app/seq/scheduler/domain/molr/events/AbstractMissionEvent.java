/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.molr.events;

import static java.util.Objects.requireNonNull;

import cern.lhc.app.seq.scheduler.domain.molr.MissionEvent;
import cern.lhc.app.seq.scheduler.domain.molr.MissionHandle;

public abstract class AbstractMissionEvent implements MissionEvent {

    private final MissionHandle handle;

    public AbstractMissionEvent(MissionHandle handle) {
        this.handle = requireNonNull(handle, "handle must not be null");
    }

    @Override
    public MissionHandle handle() {
        return handle;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((handle == null) ? 0 : handle.hashCode());
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
        AbstractMissionEvent other = (AbstractMissionEvent) obj;
        if (handle == null) {
            if (other.handle != null) {
                return false;
            }
        } else if (!handle.equals(other.handle)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [handle=" + handle + "]";
    }

}
