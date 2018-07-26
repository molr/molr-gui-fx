/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.info;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;

public class HardcodedExecutableStatisticsProvider implements ExecutableStatisticsProvider {

    // @formatter:off
    private static final Map<String, Duration> DURATIONS =
            ImmutableMap.<String, Duration>builder()
            .put("RESET OF LASLETT TUNE TRIMS", Duration.ofSeconds(3))
            .put("SEND COLLIMATORS FROM PHYSICS TO INJECTION", Duration.ofMinutes(10))
            .put("Leaf 1A", Duration.ofSeconds(11))
            .put("Leaf 1B", Duration.ofSeconds(16))
            .put("Leaf 2B", Duration.ofSeconds(16))
            .put("subSeq 2", Duration.ofSeconds(21))
            .build();
    // @formatter:on

    @Override
    public Optional<Duration> expectedDurationFor(ExecutionBlock executable) {
        return Optional.ofNullable(DURATIONS.get(executable.name()));
    }

}
