/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.util;

import static cern.lhc.app.seq.scheduler.util.DurationFormats.formatLetters;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class DurationFormatsTest {

    private static final int ONE_HOUR_FIVE_MIN = 1000 * 3600 + 5000 * 60;
    private static final int ONE_HOUR_FIVE_SEC = 1000 * 3600 + 5000;

    @Test
    public void fullLetters() {
        assertThat(formatLetters(ONE_HOUR_FIVE_MIN, false, false)).isEqualTo("0d 1h 5m 0s");
    }

    @Test
    public void suppressLeadingZeroes() {
        assertThat(formatLetters(ONE_HOUR_FIVE_MIN, true, false)).isEqualTo("1h 5m 0s");
    }

    @Test
    public void suppressTrailingZeroes() {
        assertThat(formatLetters(ONE_HOUR_FIVE_MIN, false, true)).isEqualTo("0d 1h 5m");
    }

    @Test
    public void suppressLeadingAndTrailing() {
        assertThat(formatLetters(ONE_HOUR_FIVE_MIN, true, true)).isEqualTo("1h 5m");
    }

    @Test
    public void middleIsNeverSuppressed() {
        assertThat(formatLetters(ONE_HOUR_FIVE_SEC, true, true)).isEqualTo("1h 0m 5s");
    }
}
