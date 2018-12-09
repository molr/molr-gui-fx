/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package io.molr.gui.fx.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;

public final class DurationFormats {

    private DurationFormats() {
        /* only static methods */
    }

    public static final String shortLetters(Duration duration) {
        return shortLetters(duration.toMillis());
    }

    public static final String shortLetters(long millis) {
        return formatLetters(millis, true, true);
    }

    public static final String fullLetters(Duration duration) {
        return fullLetters(duration.toMillis());
    }

    public static final String fullLetters(long millis) {
        return formatLetters(millis, false, false);
    }

    public static final String formatLetters(Duration duration, boolean suppressLeadingZeroElements,
            boolean suppressTrailingZeroElements) {
        return formatLetters(duration.toMillis(), suppressLeadingZeroElements, suppressTrailingZeroElements);
    }

    public static final String formatLetters(long millis, boolean suppressLeadingZeroElements,
            boolean suppressTrailingZeroElements) {
        String duration = DurationFormatUtils.formatDuration(millis, "d'd 'H'h 'm'm 's's'");

        if (suppressLeadingZeroElements) {
            // this is a temporary marker on the front. Like ^ in regexp.
            duration = " " + duration;
            String tmp = StringUtils.replaceOnce(duration, " 0d", StringUtils.EMPTY);
            if (tmp.length() != duration.length()) {
                duration = tmp;
                tmp = StringUtils.replaceOnce(duration, " 0h", StringUtils.EMPTY);
                if (tmp.length() != duration.length()) {
                    duration = tmp;
                    tmp = StringUtils.replaceOnce(duration, " 0m", StringUtils.EMPTY);
                    duration = tmp;
                    if (tmp.length() != duration.length()) {
                        duration = StringUtils.replaceOnce(tmp, " 0s", StringUtils.EMPTY);
                    }
                }
            }
            if (duration.length() != 0) {
                // strip the space off again
                duration = duration.substring(1);
            }
        }
        if (suppressTrailingZeroElements) {
            String tmp = StringUtils.replaceOnce(duration, " 0s", StringUtils.EMPTY);
            if (tmp.length() != duration.length()) {
                duration = tmp;
                tmp = StringUtils.replaceOnce(duration, " 0m", StringUtils.EMPTY);
                if (tmp.length() != duration.length()) {
                    duration = tmp;
                    tmp = StringUtils.replaceOnce(duration, " 0h", StringUtils.EMPTY);
                    if (tmp.length() != duration.length()) {
                        duration = StringUtils.replaceOnce(tmp, " 0d", StringUtils.EMPTY);
                    }
                }
            }
        }

        return duration;
    }

}
