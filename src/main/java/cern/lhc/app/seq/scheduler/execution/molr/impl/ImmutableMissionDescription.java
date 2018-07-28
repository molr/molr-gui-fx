package cern.lhc.app.seq.scheduler.execution.molr.impl;

import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;
import cern.lhc.app.seq.scheduler.domain.execution.demo.DemoBlock;
import cern.lhc.app.seq.scheduler.domain.molr.Line;
import cern.lhc.app.seq.scheduler.domain.molr.MissionDescription;
import cern.lhc.app.seq.scheduler.domain.molr.Page;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class ImmutableMissionDescription implements MissionDescription {

    private final ExecutionBlock root;

    public ImmutableMissionDescription(ExecutionBlock root) {
        this.root = root;
    }

    @Override
    public ExecutionBlock rootBlock() {
        return this.root;
    }
}
