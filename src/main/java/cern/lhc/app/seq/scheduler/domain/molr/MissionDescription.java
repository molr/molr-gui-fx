/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.molr;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public interface MissionDescription {

    List<? extends Line> topLevel();

    List<Line> childrenOf(Line line);


}