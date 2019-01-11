package org.iatoki.judgels.jerahmeel;

import org.iatoki.judgels.api.jophiel.JophielClientAPI;
import org.iatoki.judgels.api.jophiel.JophielPublicAPI;
import org.iatoki.judgels.jerahmeel.archive.ArchiveDao;
import org.iatoki.judgels.jerahmeel.grading.bundle.BundleGradingDao;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionDao;
import org.iatoki.judgels.jerahmeel.scorecache.ContainerProblemScoreCacheDao;
import org.iatoki.judgels.jerahmeel.scorecache.ContainerScoreCacheDao;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterDao;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetDao;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblemDao;
import org.iatoki.judgels.jerahmeel.grading.programming.ProgrammingGradingDao;
import org.iatoki.judgels.jerahmeel.submission.programming.ProgrammingSubmissionDao;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemDao;
import org.iatoki.judgels.jerahmeel.user.item.UserItemDao;
import org.iatoki.judgels.jerahmeel.statistic.point.PointStatisticService;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemScoreStatisticService;
import org.iatoki.judgels.jerahmeel.statistic.problem.ProblemStatisticService;
import org.iatoki.judgels.jerahmeel.scorecache.ProblemSetScoreCacheUtils;
import org.iatoki.judgels.jerahmeel.chapter.ChapterProgressCacheUtils;
import org.iatoki.judgels.jerahmeel.chapter.ChapterScoreCacheUtils;
import org.iatoki.judgels.jophiel.JophielClientControllerUtils;
import org.iatoki.judgels.jophiel.activity.UserActivityMessageServiceImpl;
import org.iatoki.judgels.jerahmeel.activity.ActivityLogDao;
import org.iatoki.judgels.jerahmeel.avatar.AvatarCacheDao;
import org.iatoki.judgels.jerahmeel.jid.JidCacheDao;
import org.iatoki.judgels.jerahmeel.activity.ActivityLogServiceImpl;
import org.iatoki.judgels.jerahmeel.avatar.AvatarCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionService;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @deprecated Temporary class. Will be restructured when new module system has been finalized.
 */
@Singleton
@Deprecated
public final class JerahmeelSingletonsBuilder {

    @Inject
    public JerahmeelSingletonsBuilder(
            JidCacheDao jidCacheDao, AvatarCacheDao avatarCacheDao, ActivityLogDao activityLogDao,
            JophielClientAPI jophielClientAPI, JophielPublicAPI jophielPublicAPI,
            BundleSubmissionService bundleSubmissionService, PointStatisticService pointStatisticService, ProblemScoreStatisticService problemScoreStatisticService, ProblemStatisticService problemStatisticService, ProgrammingSubmissionService programmingSubmissionService,
            ChapterProblemDao chapterProblemDao, UserItemDao userItemDao,
            ArchiveDao archiveDao, BundleSubmissionDao bundleSubmissionDao, BundleGradingDao bundleGradingDao, ContainerScoreCacheDao containerScoreCacheDao, ContainerProblemScoreCacheDao containerProblemScoreCacheDao, ProblemSetDao problemSetDao, ProblemSetProblemDao problemSetProblemDao, ProgrammingSubmissionDao programmingSubmissionDao, ProgrammingGradingDao programmingGradingDao,
            CourseChapterDao courseChapterDao) {

        JidCacheServiceImpl.buildInstance(jidCacheDao);
        AvatarCacheServiceImpl.buildInstance(avatarCacheDao);
        ActivityLogServiceImpl.buildInstance(activityLogDao);
        UserActivityMessageServiceImpl.buildInstance();

        JophielClientControllerUtils.buildInstance(JerahmeelProperties.getInstance().getRaphaelBaseUrl(), JerahmeelProperties.getInstance().getJophielBaseUrl());
        JerahmeelControllerUtils.buildInstance(jophielClientAPI, jophielPublicAPI, bundleSubmissionService, pointStatisticService, problemScoreStatisticService, problemStatisticService, programmingSubmissionService);
        ChapterProgressCacheUtils.buildInstance(chapterProblemDao, userItemDao);
        ProblemSetScoreCacheUtils.buildInstance(archiveDao, bundleSubmissionDao, bundleGradingDao, containerScoreCacheDao, containerProblemScoreCacheDao, problemSetDao, problemSetProblemDao, programmingSubmissionDao, programmingGradingDao);
        ChapterScoreCacheUtils.buildInstance(bundleSubmissionDao, bundleGradingDao, containerScoreCacheDao, containerProblemScoreCacheDao, courseChapterDao, programmingSubmissionDao, programmingGradingDao, chapterProblemDao);
    }
}
