package org.iatoki.judgels.jerahmeel.scorecache;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.iatoki.judgels.jerahmeel.ProblemScore;
import org.iatoki.judgels.jerahmeel.archive.Archive;
import org.iatoki.judgels.jerahmeel.archive.ArchiveDao;
import org.iatoki.judgels.jerahmeel.archive.ArchiveModel;
import org.iatoki.judgels.jerahmeel.archive.ArchiveServiceUtils;
import org.iatoki.judgels.jerahmeel.grading.bundle.BundleGradingDao;
import org.iatoki.judgels.jerahmeel.grading.bundle.BundleGradingModel;
import org.iatoki.judgels.jerahmeel.grading.programming.ProgrammingGradingDao;
import org.iatoki.judgels.jerahmeel.grading.programming.ProgrammingGradingModel;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetDao;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetModel;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetModel_;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblemDao;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblemModel;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblemType;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionDao;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionModel;
import org.iatoki.judgels.jerahmeel.submission.programming.ProgrammingSubmissionDao;
import org.iatoki.judgels.jerahmeel.submission.programming.ProgrammingSubmissionModel;

import java.util.List;
import java.util.Map;

public final class ProblemSetScoreCacheUtils {

    private static ProblemSetScoreCacheUtils instance;

    private final ArchiveDao archiveDao;
    private final BundleSubmissionDao bundleSubmissionDao;
    private final BundleGradingDao bundleGradingDao;
    private final ContainerScoreCacheDao containerScoreCacheDao;
    private final ContainerProblemScoreCacheDao containerProblemScoreCacheDao;
    private final ProblemSetDao problemSetDao;
    private final ProblemSetProblemDao problemSetProblemDao;
    private final ProgrammingSubmissionDao programmingSubmissionDao;
    private final ProgrammingGradingDao programmingGradingDao;

    public ProblemSetScoreCacheUtils(ArchiveDao archiveDao, BundleSubmissionDao bundleSubmissionDao, BundleGradingDao bundleGradingDao, ContainerScoreCacheDao containerScoreCacheDao, ContainerProblemScoreCacheDao containerProblemScoreCacheDao, ProblemSetDao problemSetDao, ProblemSetProblemDao problemSetProblemDao, ProgrammingSubmissionDao programmingSubmissionDao, ProgrammingGradingDao programmingGradingDao) {
        this.archiveDao = archiveDao;
        this.bundleSubmissionDao = bundleSubmissionDao;
        this.bundleGradingDao = bundleGradingDao;
        this.containerScoreCacheDao = containerScoreCacheDao;
        this.containerProblemScoreCacheDao = containerProblemScoreCacheDao;
        this.problemSetDao = problemSetDao;
        this.problemSetProblemDao = problemSetProblemDao;
        this.programmingSubmissionDao = programmingSubmissionDao;
        this.programmingGradingDao = programmingGradingDao;
    }

    public static synchronized void buildInstance(ArchiveDao archiveDao, BundleSubmissionDao bundleSubmissionDao, BundleGradingDao bundleGradingDao, ContainerScoreCacheDao containerScoreCacheDao, ContainerProblemScoreCacheDao containerProblemScoreCacheDao, ProblemSetDao problemSetDao, ProblemSetProblemDao problemSetProblemDao, ProgrammingSubmissionDao programmingSubmissionDao, ProgrammingGradingDao programmingGradingDao) {
        if (instance != null) {
            throw new UnsupportedOperationException("ProblemSetScoreCacheUtils instance has already been built");
        }
        instance = new ProblemSetScoreCacheUtils(archiveDao, bundleSubmissionDao, bundleGradingDao, containerScoreCacheDao, containerProblemScoreCacheDao, problemSetDao, problemSetProblemDao, programmingSubmissionDao, programmingGradingDao);
    }

    public static ProblemSetScoreCacheUtils getInstance() {
        if (instance == null) {
            throw new UnsupportedOperationException("ProblemSetScoreCacheUtils instance has not been built");
        }
        return instance;
    }

    public double getArchiveScore(String userJid, String archiveJid) {
        if (containerScoreCacheDao.existsByUserJidAndContainerJid(userJid, archiveJid)) {
            ContainerScoreCacheModel containerScoreCacheModel = containerScoreCacheDao.getByUserJidAndContainerJid(userJid, archiveJid);
            return containerScoreCacheModel.score;
        }

        double archiveScore = getArchiveScoreWithoutCache(userJid, archiveJid);

        ContainerScoreCacheServiceUtils.addToContainerScoreCache(containerScoreCacheDao, userJid, archiveJid, archiveScore);

        return archiveScore;
    }

    public double getArchiveScoreWithoutCache(String userJid, String archiveJid) {
        double archiveScore = 0;
        List<ProblemSetModel> problemSetModels = problemSetDao.findSortedByFiltersEq("id", "asc", "", ImmutableMap.of(ProblemSetModel_.archiveJid, archiveJid), 0, -1);
        for (ProblemSetModel problemSetModel : problemSetModels) {
            archiveScore += getUserTotalScoreFromProblemSetModelAndProblemSetProblemModels(userJid, problemSetModel, problemSetProblemDao.getByProblemSetJid(problemSetModel.jid));
        }
        List<Archive> subArchives = ArchiveServiceUtils.getChildArchives(archiveDao, archiveJid);
        for (Archive subArchive : subArchives) {
            archiveScore += getArchiveScore(userJid, subArchive.getJid());
        }

        return archiveScore;
    }

    public double getUserTotalScoreFromProblemSetModelAndProblemSetProblemModels(String userJid, ProblemSetModel problemSetModel, List<ProblemSetProblemModel> problemSetProblemModels) {
        if (containerScoreCacheDao.existsByUserJidAndContainerJid(userJid, problemSetModel.jid)) {
            ContainerScoreCacheModel containerScoreCacheModel = containerScoreCacheDao.getByUserJidAndContainerJid(userJid, problemSetModel.jid);
            return containerScoreCacheModel.score;
        }

        double problemSetScore = getUserTotalScoreFromProblemSetModelAndProblemSetProblemModelsWithoutCache(userJid, problemSetModel, problemSetProblemModels);

        ContainerScoreCacheServiceUtils.addToContainerScoreCache(containerScoreCacheDao, userJid, problemSetModel.jid, problemSetScore);
        return problemSetScore;
    }

    public double getUserTotalScoreFromProblemSetModelAndProblemSetProblemModelsWithoutCache(String userJid, ProblemSetModel problemSetModel, List<ProblemSetProblemModel> problemSetProblemModels) {
        return getUserTotalScoreFromProblemSetProblemModels(userJid, problemSetProblemModels);
    }

    public double getUserTotalScoreFromProblemSetProblemModels(String userJid, List<ProblemSetProblemModel> problemSetProblemModels) {
        double totalScore = 0;
        for (ProblemSetProblemModel problemSetProblemModel : problemSetProblemModels) {
            double problemSetProblemScore = getUserMaxScoreFromProblemSetProblemModel(userJid, problemSetProblemModel);
            if (Double.compare(ProblemScore.MINIMUM_SCORE, problemSetProblemScore) != 0) {
                totalScore += problemSetProblemScore;
            }
        }

        return totalScore;
    }

    public double getUserMaxScoreFromProblemSetProblemModel(String userJid, ProblemSetProblemModel problemSetProblemModel) {
        if (containerProblemScoreCacheDao.existsByUserJidContainerJidAndProblemJid(userJid, problemSetProblemModel.problemSetJid, problemSetProblemModel.problemJid)) {
            ContainerProblemScoreCacheModel containerProblemScoreCacheModel = containerProblemScoreCacheDao.getByUserJidContainerJidAndProblemJid(userJid, problemSetProblemModel.problemSetJid, problemSetProblemModel.problemJid);
            return containerProblemScoreCacheModel.score;
        }

        double maxScore = ProblemScore.MINIMUM_SCORE;
        if (problemSetProblemModel.type.equals(ProblemSetProblemType.BUNDLE.name())) {
            List<BundleSubmissionModel> bundleSubmissionModels = bundleSubmissionDao.getByContainerJidAndUserJidAndProblemJid(problemSetProblemModel.problemSetJid, userJid, problemSetProblemModel.problemJid);

            Map<String, List<BundleGradingModel>> gradingModelsMap = bundleGradingDao.getBySubmissionJids(Lists.transform(bundleSubmissionModels, m -> m.jid));

            for (BundleSubmissionModel bundleSubmissionModel : bundleSubmissionModels) {
                double submissionScore = gradingModelsMap.get(bundleSubmissionModel.jid).get(gradingModelsMap.get(bundleSubmissionModel.jid).size() - 1).score;
                if (submissionScore > maxScore) {
                    maxScore = submissionScore;
                }
            }
        } else if (problemSetProblemModel.type.equals(ProblemSetProblemType.PROGRAMMING.name())) {
            List<ProgrammingSubmissionModel> programmingSubmissionModels = programmingSubmissionDao.getByContainerJidAndUserJidAndProblemJid(problemSetProblemModel.problemSetJid, userJid, problemSetProblemModel.problemJid);

            Map<String, List<ProgrammingGradingModel>> gradingModelsMap = programmingGradingDao.getBySubmissionJids(Lists.transform(programmingSubmissionModels, m -> m.jid));

            for (ProgrammingSubmissionModel programmingSubmissionModel : programmingSubmissionModels) {
                double submissionScore = gradingModelsMap.get(programmingSubmissionModel.jid).get(gradingModelsMap.get(programmingSubmissionModel.jid).size() - 1).score;
                if (submissionScore > maxScore) {
                    maxScore = submissionScore;
                }
            }
        }

        ContainerProblemScoreCacheServiceUtils.addToContainerProblemScoreCache(containerProblemScoreCacheDao, userJid, problemSetProblemModel.problemJid, problemSetProblemModel.problemJid, maxScore);
        return maxScore;
    }

    public void updateProblemSetAndArchivesScoreCache(String userJid, String problemSetJid, double deltaScore) {
        ProblemSetModel problemSetModel = problemSetDao.findByJid(problemSetJid);
        if (containerScoreCacheDao.existsByUserJidAndContainerJid(userJid, problemSetJid)) {
            ContainerScoreCacheModel containerScoreCacheModel = containerScoreCacheDao.getByUserJidAndContainerJid(userJid, problemSetJid);
            containerScoreCacheModel.score = containerScoreCacheModel.score + deltaScore;

            containerScoreCacheDao.edit(containerScoreCacheModel, "cacheAfterGradeUpdater", "localhost");
        } else {
            double problemSetScore = ProblemSetScoreCacheUtils.getInstance().getUserTotalScoreFromProblemSetModelAndProblemSetProblemModelsWithoutCache(userJid, problemSetModel, problemSetProblemDao.getByProblemSetJid(problemSetModel.jid));

            ContainerScoreCacheServiceUtils.addToContainerScoreCache(containerScoreCacheDao, userJid, problemSetModel.jid, problemSetScore);
        }

        ArchiveModel archiveModel = archiveDao.findByJid(problemSetModel.archiveJid);
        do {
            updateArchiveScoreCache(userJid, archiveModel.jid, deltaScore);

            if (archiveModel.parentJid.isEmpty()) {
                archiveModel = null;
            } else {
                archiveModel = archiveDao.findByJid(archiveModel.parentJid);
            }
        } while (archiveModel != null);
    }

    public void updateArchiveScoreCache(String userJid, String archiveJid, double deltaScore) {
        if (containerScoreCacheDao.existsByUserJidAndContainerJid(userJid, archiveJid)) {
            ContainerScoreCacheModel containerScoreCacheModel = containerScoreCacheDao.getByUserJidAndContainerJid(userJid, archiveJid);
            containerScoreCacheModel.score = containerScoreCacheModel.score + deltaScore;

            containerScoreCacheDao.edit(containerScoreCacheModel, "cacheAfterGradeUpdater", "localhost");
        } else {
            double archiveScore = ProblemSetScoreCacheUtils.getInstance().getArchiveScoreWithoutCache(userJid, archiveJid);

            ContainerScoreCacheServiceUtils.addToContainerScoreCache(containerScoreCacheDao, userJid, archiveJid, archiveScore);
        }
    }
}
