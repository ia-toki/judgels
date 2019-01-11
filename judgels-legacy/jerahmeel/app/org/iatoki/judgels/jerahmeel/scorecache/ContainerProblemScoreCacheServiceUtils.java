package org.iatoki.judgels.jerahmeel.scorecache;

public final class ContainerProblemScoreCacheServiceUtils {

    private ContainerProblemScoreCacheServiceUtils() {
        // prevent instantiation
    }

    public static void addToContainerProblemScoreCache(ContainerProblemScoreCacheDao containerProblemScoreCacheDao, String userJid, String containerJid, String problemJid, double score) {
        ContainerProblemScoreCacheModel containerProblemScoreCacheModel = new ContainerProblemScoreCacheModel();
        containerProblemScoreCacheModel.userJid = userJid;
        containerProblemScoreCacheModel.containerJid = containerJid;
        containerProblemScoreCacheModel.problemJid = problemJid;
        containerProblemScoreCacheModel.score = score;

        containerProblemScoreCacheDao.persist(containerProblemScoreCacheModel, "cacheLazyUpdater", "localhost");
    }
}
