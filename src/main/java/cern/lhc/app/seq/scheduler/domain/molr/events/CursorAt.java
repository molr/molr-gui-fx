/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.molr.events;

import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;
import org.molr.commons.domain.MissionHandle;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class CursorAt extends AbstractMissionEvent {

    private final Set<ExecutionBlock> lines;

    public CursorAt(MissionHandle handle, Set<ExecutionBlock> lines) {
        super(handle);
        this.lines = ImmutableSet.copyOf(lines);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((lines == null) ? 0 : lines.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CursorAt other = (CursorAt) obj;
        if (lines == null) {
            if (other.lines != null) {
                return false;
            }
        } else if (!lines.equals(other.lines)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CursorAt [lines=" + lines + "]";
    }

}
