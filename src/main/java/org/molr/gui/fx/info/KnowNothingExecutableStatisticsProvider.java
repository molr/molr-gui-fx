/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.molr.gui.fx.info;

import org.molr.commons.domain.Block;

import java.time.Duration;
import java.util.Optional;

public class KnowNothingExecutableStatisticsProvider implements ExecutableStatisticsProvider {

    @Override
    public Optional<Duration> expectedDurationFor(Block executable) {
        return Optional.empty();
    }

}
