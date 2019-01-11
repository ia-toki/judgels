package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import com.google.common.collect.ImmutableMap;
import org.iatoki.judgels.play.Page;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public final class ProblemScoreStatisticServiceImpl implements ProblemScoreStatisticService {

    private final ProblemScoreStatisticDao problemScoreStatisticDao;
    private final ProblemScoreStatisticEntryDao problemScoreStatisticEntryDao;

    @Inject
    public ProblemScoreStatisticServiceImpl(ProblemScoreStatisticDao problemScoreStatisticDao, ProblemScoreStatisticEntryDao problemScoreStatisticEntryDao) {
        this.problemScoreStatisticDao = problemScoreStatisticDao;
        this.problemScoreStatisticEntryDao = problemScoreStatisticEntryDao;
    }

    @Override
    public boolean problemScoreStatisticExists(String problemJid) {
        return problemScoreStatisticDao.countByFiltersEq("", ImmutableMap.of(ProblemScoreStatisticModel_.problemJid, problemJid)) != 0;
    }

    @Override
    public ProblemScoreStatistic getLatestProblemScoreStatisticWithPagination(String problemJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        ProblemScoreStatisticModel problemScoreStatisticModel = problemScoreStatisticDao.findSortedByFiltersEq("time", "desc", "", ImmutableMap.of(ProblemScoreStatisticModel_.problemJid, problemJid), 0, 1).get(0);
        long totalRowCount = problemScoreStatisticEntryDao.countByFiltersEq(filterString, ImmutableMap.of(ProblemScoreStatisticEntryModel_.problemScoreStatisticJid, problemScoreStatisticModel.jid));
        List<ProblemScoreStatisticEntryModel> problemStatisticEntryModels = problemScoreStatisticEntryDao.findSortedByFiltersEq(orderBy, orderDir, filterString, ImmutableMap.of(ProblemScoreStatisticEntryModel_.problemScoreStatisticJid, problemScoreStatisticModel.jid), pageIndex, pageSize);
        List<ProblemScoreStatisticEntry> problemScoreStatisticEntries = problemStatisticEntryModels.stream().map(m -> new ProblemScoreStatisticEntry(m.userJid, m.score, m.time)).collect(Collectors.toList());

        return new ProblemScoreStatistic(new Page<>(problemScoreStatisticEntries, totalRowCount, pageIndex, pageSize), problemScoreStatisticModel.problemJid, problemScoreStatisticModel.time);
    }

    @Override
    public void updateProblemStatistic(List<ProblemScoreStatisticEntry> problemScoreStatisticEntries, String problemJid, long time) {
        ProblemScoreStatisticModel problemScoreStatisticModel = new ProblemScoreStatisticModel();
        problemScoreStatisticModel.problemJid = problemJid;
        problemScoreStatisticModel.time = time;

        problemScoreStatisticDao.persist(problemScoreStatisticModel, "statisticUpdater", "statisticUpdater");

        for (ProblemScoreStatisticEntry problemScoreStatisticEntry : problemScoreStatisticEntries) {
            ProblemScoreStatisticEntryModel problemScoreStatisticEntryModel = new ProblemScoreStatisticEntryModel();
            problemScoreStatisticEntryModel.problemScoreStatisticJid = problemScoreStatisticModel.jid;
            problemScoreStatisticEntryModel.score = problemScoreStatisticEntry.getScore();
            problemScoreStatisticEntryModel.userJid = problemScoreStatisticEntry.getUserJid();
            problemScoreStatisticEntryModel.time = problemScoreStatisticEntry.getTime();

            problemScoreStatisticEntryDao.persist(problemScoreStatisticEntryModel, "statisticUpdater", "statisticUpdater");
        }
    }
}
