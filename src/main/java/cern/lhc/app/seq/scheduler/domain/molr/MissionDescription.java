/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.molr;

import java.util.Set;
import java.util.SortedSet;

public interface MissionDescription {

    SortedSet<Line> topLevel(Page page);

    Set<Line> childrenOf(Line line);

    Set<Page> pages();

}