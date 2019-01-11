package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import com.google.inject.ImplementedBy;

import java.util.List;

@ImplementedBy(ProblemScoreStatisticServiceImpl.class)
public interface ProblemScoreStatisticService {

    boolean problemScoreStatisticExists(String problemJid);

    ProblemScoreStatistic getLatestProblemScoreStatisticWithPagination(String problemJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    void updateProblemStatistic(List<ProblemScoreStatisticEntry> problemStatisticEntries, String problemJid, long time);
}
