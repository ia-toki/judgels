package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import org.iatoki.judgels.play.Page;

public final class ProblemScoreStatistic {

    private final Page<ProblemScoreStatisticEntry> pageOfProblemScoreStatisticEntries;
    private final String problemJid;
    private final long time;

    public ProblemScoreStatistic(Page<ProblemScoreStatisticEntry> pageOfProblemScoreStatisticEntries, String problemJid, long time) {
        this.pageOfProblemScoreStatisticEntries = pageOfProblemScoreStatisticEntries;
        this.problemJid = problemJid;
        this.time = time;
    }

    public Page<ProblemScoreStatisticEntry> getPageOfProblemScoreStatisticEntries() {
        return pageOfProblemScoreStatisticEntries;
    }

    public String getProblemJid() {
        return problemJid;
    }

    public long getTime() {
        return time;
    }
}
