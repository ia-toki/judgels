package org.iatoki.judgels.jerahmeel.statistic.point;

import org.iatoki.judgels.play.Page;

public final class PointStatistic {

    private final Page<PointStatisticEntry> pageOfPointStatisticEntries;
    private final long time;

    public PointStatistic(Page<PointStatisticEntry> pageOfPointStatisticEntries, long time) {
        this.pageOfPointStatisticEntries = pageOfPointStatisticEntries;
        this.time = time;
    }

    public Page<PointStatisticEntry> getPageOfPointStatisticEntries() {
        return pageOfPointStatisticEntries;
    }

    public long getTime() {
        return time;
    }
}
