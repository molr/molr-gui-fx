/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.molr;

import java.util.Comparator;

public final class Comparators {

    private static final Comparator<Line> LINE_COMPARATOR = createLineComparator();
    private static final Comparator<Page> PAGE_COMPARATOR = createPageComparator();

    private Comparators() {
        /* only static methods */
    }

    public static final Comparator<Line> lineComparator() {
        return LINE_COMPARATOR;
    }

    public static final Comparator<Page> pageComparator() {
        return PAGE_COMPARATOR;
    }

    private static final Comparator<Line> createLineComparator() {
        Comparator<Line> comparingPage = Comparator.comparing(l -> l.page().number());
        return comparingPage.thenComparing(Line::number);
    }

    private static Comparator<Page> createPageComparator() {
        return Comparator.comparing(p -> p.number());
    }

}
