package org.iatoki.judgels.jerahmeel.statistic.problem;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemStatistic;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemStatisticEntry;

import java.util.List;

@ImplementedBy(ProblemStatisticServiceImpl.class)
public interface ProblemStatisticService {

    boolean problemStatisticExists();

    ProblemStatistic getLatestProblemStatisticWithPagination(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    void updateProblemStatistic(List<ProblemStatisticEntry> problemStatisticEntries, long time);
}
