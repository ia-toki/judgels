package org.iatoki.judgels.jerahmeel.statistic.problem;

import com.google.common.collect.ImmutableMap;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemStatistic;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemStatisticEntry;
import org.iatoki.judgels.play.Page;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public final class ProblemStatisticServiceImpl implements ProblemStatisticService {

    private final ProblemStatisticDao problemStatisticDao;
    private final ProblemStatisticEntryDao problemStatisticEntryDao;

    @Inject
    public ProblemStatisticServiceImpl(ProblemStatisticDao problemStatisticDao, ProblemStatisticEntryDao problemStatisticEntryDao) {
        this.problemStatisticDao = problemStatisticDao;
        this.problemStatisticEntryDao = problemStatisticEntryDao;
    }

    @Override
    public boolean problemStatisticExists() {
        return problemStatisticDao.countByFilters("") != 0;
    }

    @Override
    public ProblemStatistic getLatestProblemStatisticWithPagination(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        ProblemStatisticModel problemStatisticModel = problemStatisticDao.findSortedByFilters("time", "desc", "", 0, 1).get(0);
        long totalRowCount = problemStatisticEntryDao.countByFiltersEq(filterString, ImmutableMap.of(ProblemStatisticEntryModel_.problemStatisticJid, problemStatisticModel.jid));
        List<ProblemStatisticEntryModel> problemStatisticEntryModels = problemStatisticEntryDao.findSortedByFiltersEq(orderBy, orderDir, filterString, ImmutableMap.of(ProblemStatisticEntryModel_.problemStatisticJid, problemStatisticModel.jid), pageIndex * pageSize, pageSize);
        List<ProblemStatisticEntry> problemStatisticEntries = problemStatisticEntryModels.stream().map(m -> new ProblemStatisticEntry(m.problemJid, m.totalSubmissions)).collect(Collectors.toList());

        return new ProblemStatistic(new Page<>(problemStatisticEntries, totalRowCount, pageIndex, pageSize), problemStatisticModel.time);
    }

    @Override
    public void updateProblemStatistic(List<ProblemStatisticEntry> problemStatisticEntries, long time) {
        ProblemStatisticModel problemStatisticModel = new ProblemStatisticModel();
        problemStatisticModel.time = time;

        problemStatisticDao.persist(problemStatisticModel, "statisticUpdater", "statisticUpdater");

        for (ProblemStatisticEntry problemStatisticEntry : problemStatisticEntries) {
            ProblemStatisticEntryModel problemStatisticEntryModel = new ProblemStatisticEntryModel();
            problemStatisticEntryModel.problemStatisticJid = problemStatisticModel.jid;
            problemStatisticEntryModel.problemJid = problemStatisticEntry.getProblemJid();
            problemStatisticEntryModel.totalSubmissions = problemStatisticEntry.getTotalSubmissions();

            problemStatisticEntryDao.persist(problemStatisticEntryModel, "statisticUpdater", "statisticUpdater");
        }
    }
}
