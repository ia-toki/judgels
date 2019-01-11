package org.iatoki.judgels.jerahmeel.submission;

import org.iatoki.judgels.jerahmeel.ProblemScore;
import org.iatoki.judgels.jerahmeel.scorecache.ContainerProblemScoreCacheDao;
import org.iatoki.judgels.jerahmeel.scorecache.ContainerProblemScoreCacheModel;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmission;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmission;

import java.util.List;

public final class JerahmeelSubmissionServiceUtils {

    private JerahmeelSubmissionServiceUtils() {
        // prevent instantiation
    }

    public static double countBundleSubmissionsMaxScore(List<BundleSubmission> bundleSubmissions) {
        if (bundleSubmissions.isEmpty()) {
            return 0;
        }

        double maxScore = ProblemScore.MINIMUM_SCORE;
        for (BundleSubmission bundleSubmission : bundleSubmissions) {
            if (bundleSubmission.getLatestScore() > maxScore) {
                maxScore = bundleSubmission.getLatestScore();
            }
        }

        return maxScore;
    }

    public static double countProgrammingSubmissionsMaxScore(List<ProgrammingSubmission> programmingSubmissions) {
        if (programmingSubmissions.isEmpty()) {
            return 0;
        }

        double maxScore = ProblemScore.MINIMUM_SCORE;
        for (ProgrammingSubmission programmingSubmission : programmingSubmissions) {
            if (programmingSubmission.getLatestScore() > maxScore) {
                maxScore = programmingSubmission.getLatestScore();
            }
        }

        return maxScore;
    }

    public static void createContainerProblemScoreCache(ContainerProblemScoreCacheDao containerProblemScoreCacheDao, String userJid, String containerJid, String problemJid, double maxScore) {
        ContainerProblemScoreCacheModel containerProblemScoreCacheModel = new ContainerProblemScoreCacheModel();
        containerProblemScoreCacheModel.containerJid = containerJid;
        containerProblemScoreCacheModel.userJid = userJid;
        containerProblemScoreCacheModel.problemJid = problemJid;
        containerProblemScoreCacheModel.score = maxScore;
        containerProblemScoreCacheDao.persist(containerProblemScoreCacheModel, "cacheAfterGradeUpdater", "localhost");
    }

    public static void updateContainerProblemScoreCache(ContainerProblemScoreCacheDao containerProblemScoreCacheDao, String userJid, String containerJid, String problemJid, double maxScore) {
        ContainerProblemScoreCacheModel containerProblemScoreCacheModel = containerProblemScoreCacheDao.getByUserJidContainerJidAndProblemJid(userJid, containerJid, problemJid);
        containerProblemScoreCacheModel.score = maxScore;
        containerProblemScoreCacheDao.edit(containerProblemScoreCacheModel, "cacheAfterGradeUpdater", "localhost");
    }
}
