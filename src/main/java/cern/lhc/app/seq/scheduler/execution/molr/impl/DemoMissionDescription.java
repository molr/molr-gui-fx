package cern.lhc.app.seq.scheduler.execution.molr.impl;

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

public class DemoMissionDescription implements MissionDescription {

    private final DemoBlock root;

    public DemoMissionDescription(DemoBlock root) {
        this.root = root;
    }

    @Override
    public List<Line> topLevel() {
        return childLines(root);
    }

    private List<Line> childLines(DemoBlock root) {
        return root.children().stream().map(c -> (Line) c).collect(toList());
    }

    @Override
    public List<Line> childrenOf(Line line) {
        return childLines((DemoBlock) line);
    }

}
