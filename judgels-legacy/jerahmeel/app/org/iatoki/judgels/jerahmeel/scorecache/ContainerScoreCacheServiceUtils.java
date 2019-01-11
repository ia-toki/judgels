package org.iatoki.judgels.jerahmeel.scorecache;

public final class ContainerScoreCacheServiceUtils {

    private ContainerScoreCacheServiceUtils() {
        // prevent instantiation
    }

    public static void addToContainerScoreCache(ContainerScoreCacheDao containerScoreCacheDao, String userJid, String containerJid, double score) {
        ContainerScoreCacheModel containerScoreCacheModel = new ContainerScoreCacheModel();
        containerScoreCacheModel.userJid = userJid;
        containerScoreCacheModel.containerJid = containerJid;
        containerScoreCacheModel.score = score;

        containerScoreCacheDao.persist(containerScoreCacheModel, "cacheLazyUpdater", "localhost");
    }
}
