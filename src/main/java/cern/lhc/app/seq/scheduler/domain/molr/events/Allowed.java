/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.molr.events;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import cern.lhc.app.seq.scheduler.domain.molr.MissionCommand;
import cern.lhc.app.seq.scheduler.domain.molr.MissionHandle;

public class Allowed extends AbstractMissionEvent {

    private final Set<Class<? extends MissionCommand>> commands;

    public Allowed(MissionHandle handle, Set<Class<? extends MissionCommand>> commands) {
        super(handle);
        requireNonNull(commands, "commands must not be null");
        this.commands = ImmutableSet.copyOf(commands);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((commands == null) ? 0 : commands.hashCode());
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
        Allowed other = (Allowed) obj;
        if (commands == null) {
            if (other.commands != null) {
                return false;
            }
        } else if (!commands.equals(other.commands)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Allowed [commands=" + commands + "]";
    }

}
