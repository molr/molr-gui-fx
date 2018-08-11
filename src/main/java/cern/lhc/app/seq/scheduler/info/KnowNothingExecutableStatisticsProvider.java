/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.info;

import org.molr.commons.api.domain.Block;

import java.time.Duration;
import java.util.Optional;

public class KnowNothingExecutableStatisticsProvider implements ExecutableStatisticsProvider {

    @Override
    public Optional<Duration> expectedDurationFor(Block executable) {
        return Optional.empty();
    }

}
