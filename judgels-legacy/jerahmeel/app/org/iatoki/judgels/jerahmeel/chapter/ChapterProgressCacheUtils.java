package org.iatoki.judgels.jerahmeel.chapter;

import org.iatoki.judgels.jerahmeel.user.item.UserItemStatus;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemDao;
import org.iatoki.judgels.jerahmeel.user.item.UserItemDao;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemModel;
import org.iatoki.judgels.jerahmeel.user.item.UserItemModel;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmission;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmission;

import java.util.List;

public final class ChapterProgressCacheUtils {

    private static ChapterProgressCacheUtils instance;

    private final ChapterProblemDao chapterProblemDao;
    private final UserItemDao userItemDao;

    public ChapterProgressCacheUtils(ChapterProblemDao chapterProblemDao, UserItemDao userItemDao) {
        this.chapterProblemDao = chapterProblemDao;
        this.userItemDao = userItemDao;
    }

    public static synchronized void buildInstance(ChapterProblemDao chapterProblemDao, UserItemDao userItemDao) {
        if (instance != null) {
            throw new UnsupportedOperationException("ChapterProgressCacheUtils instance has already been built");
        }
        instance = new ChapterProgressCacheUtils(chapterProblemDao, userItemDao);
    }

    public static ChapterProgressCacheUtils getInstance() {
        if (instance == null) {
            throw new UnsupportedOperationException("ChapterProgressCacheUtils instance has not been built");
        }
        return instance;
    }

    public void updateChapterProblemProgressWithBundleSubmissions(String userJid, String containerJid, String problemJid, List<BundleSubmission> bundleSubmissions) {
        boolean completed = false;
        for (int i = 0; (!completed) && (i < bundleSubmissions.size()); ++i) {
            if (Double.compare(bundleSubmissions.get(i).getLatestScore(), 100) == 0) {
                completed = true;
            }
        }

        updateChapterProblemProgress(userJid, containerJid, problemJid, completed);
    }

    public void updateChapterProblemProgressWithProgrammingSubmissions(String userJid, String containerJid, String problemJid, List<ProgrammingSubmission> programmingSubmissions) {
        boolean completed = false;
        for (int i = 0; (!completed) && (i < programmingSubmissions.size()); ++i) {
            if (Double.compare(programmingSubmissions.get(i).getLatestScore(), 100) == 0) {
                completed = true;
            }
        }

        updateChapterProblemProgress(userJid, containerJid, problemJid, completed);
    }

    private void updateChapterProblemProgress(String userJid, String containerJid, String problemJid, boolean completed) {
        if (userItemDao.existsByUserJidAndItemJid(userJid, problemJid)) {
            UserItemModel userItemModel = userItemDao.findByUserJidAndItemJid(userJid, problemJid);
            if (completed) {
                userItemModel.status = UserItemStatus.COMPLETED.name();
            } else {
                userItemModel.status = UserItemStatus.VIEWED.name();
            }
            userItemDao.edit(userItemModel, userJid, userItemModel.ipUpdate);
        } else {
            UserItemModel userItemModel = new UserItemModel();
            userItemModel.userJid = userJid;
            userItemModel.itemJid = problemJid;
            if (completed) {
                userItemModel.status = UserItemStatus.COMPLETED.name();
            } else {
                userItemModel.status = UserItemStatus.VIEWED.name();
            }
            userItemDao.persist(userItemModel, userJid, "localhost");
        }

        userItemDao.flush();

        boolean chapterCompleted = true;
        List<ChapterProblemModel> chapterProblemModels = chapterProblemDao.getByChapterJid(containerJid);
        for (ChapterProblemModel chapterProblemModel : chapterProblemModels) {
            if (!userItemDao.existsByUserJidItemJidAndStatus(userJid, chapterProblemModel.problemJid, UserItemStatus.COMPLETED.name())) {
                chapterCompleted = false;
                break;
            }
        }

        if (userItemDao.existsByUserJidAndItemJid(userJid, containerJid)) {
            UserItemModel userItemModel = userItemDao.findByUserJidAndItemJid(userJid, containerJid);
            if (chapterCompleted) {
                userItemModel.status = UserItemStatus.COMPLETED.name();
            } else {
                userItemModel.status = UserItemStatus.VIEWED.name();
            }
            userItemDao.edit(userItemModel, userJid, userItemModel.ipUpdate);
        } else {
            UserItemModel userItemModel = new UserItemModel();
            userItemModel.userJid = userJid;
            userItemModel.itemJid = containerJid;
            if (completed) {
                userItemModel.status = UserItemStatus.COMPLETED.name();
            } else {
                userItemModel.status = UserItemStatus.VIEWED.name();
            }
            userItemDao.persist(userItemModel, userJid, "localhost");
        }
    }
}
