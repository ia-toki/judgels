package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import org.iatoki.judgels.play.Page;

public final class ProblemStatistic {

    private final Page<ProblemStatisticEntry> pageOfProblemStatisticEntries;
    private final long time;

    public ProblemStatistic(Page<ProblemStatisticEntry> pageOfProblemStatisticEntries, long time) {
        this.pageOfProblemStatisticEntries = pageOfProblemStatisticEntries;
        this.time = time;
    }

    public Page<ProblemStatisticEntry> getPageOfProblemStatisticEntries() {
        return pageOfProblemStatisticEntries;
    }

    public long getTime() {
        return time;
    }
}
