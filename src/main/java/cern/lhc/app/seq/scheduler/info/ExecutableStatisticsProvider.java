/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.info;

import java.time.Duration;
import java.util.Optional;

import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;

public interface ExecutableStatisticsProvider {

    public Optional<Duration> expectedDurationFor(ExecutionBlock executable);

}
