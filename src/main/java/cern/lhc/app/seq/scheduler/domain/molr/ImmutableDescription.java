/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.molr;

import static cern.lhc.app.seq.scheduler.domain.molr.Comparators.lineComparator;
import static cern.lhc.app.seq.scheduler.domain.molr.Comparators.pageComparator;

import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

/**
 * Describes a mission in human readable form.
 * 
 * @author kfuchsbe
 */
public class ImmutableDescription implements MissionDescription {

    private final SortedSet<Line> topLevel;
    private final SetMultimap<Line, Line> children;

    public ImmutableDescription(Set<Line> topLevel, Multimap<Line, Line> treeStructure) {
        this.topLevel = ImmutableSortedSet.copyOf(lineComparator(), topLevel);
        // @formatter:off
        this.children = ImmutableSetMultimap.<Line, Line> builder()
                .orderValuesBy(lineComparator())
                .putAll(treeStructure).build();
        // @formatter:on
    }

    @Override
    public SortedSet<Line> topLevel(Page page) {
        return null;//this.topLevel.stream().fi;
    }
    
    @Override
    public Set<Line> childrenOf(Line line) {
        return this.children.get(line);
    }

    @Override
    public Set<Page> pages() {
        // @formatter:off
        return topLevel.stream()
                .map(Line::page)
                .sorted(pageComparator())
                .collect(Collectors.toSet());
        // @formatter:on
    }

}
