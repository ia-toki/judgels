package org.iatoki.judgels.jerahmeel.submission.bundle;

import org.iatoki.judgels.jerahmeel.jid.JerahmeelJidUtils;
import org.iatoki.judgels.jerahmeel.archive.ArchiveDao;
import org.iatoki.judgels.jerahmeel.chapter.ChapterProgressCacheUtils;
import org.iatoki.judgels.jerahmeel.chapter.ChapterScoreCacheUtils;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemDao;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterDao;
import org.iatoki.judgels.jerahmeel.grading.bundle.BundleGradingDao;
import org.iatoki.judgels.jerahmeel.grading.bundle.BundleGradingModel;
import org.iatoki.judgels.jerahmeel.grading.programming.ProgrammingGradingDao;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetDao;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblemDao;
import org.iatoki.judgels.jerahmeel.scorecache.ContainerProblemScoreCacheDao;
import org.iatoki.judgels.jerahmeel.scorecache.ContainerScoreCacheDao;
import org.iatoki.judgels.jerahmeel.scorecache.ProblemSetScoreCacheUtils;
import org.iatoki.judgels.jerahmeel.submission.JerahmeelSubmissionServiceUtils;
import org.iatoki.judgels.jerahmeel.submission.programming.ProgrammingSubmissionDao;
import org.iatoki.judgels.jerahmeel.user.item.UserItemDao;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleAnswer;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmission;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleProblemGrader;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionService;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.AbstractBundleSubmissionServiceImpl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public final class BundleSubmissionServiceImpl extends AbstractBundleSubmissionServiceImpl<BundleSubmissionModel, BundleGradingModel> implements BundleSubmissionService {

    private final ArchiveDao archiveDao;
    private final BundleSubmissionDao bundleSubmissionDao;
    private final BundleGradingDao bundleGradingDao;
    private final ContainerScoreCacheDao containerScoreCacheDao;
    private final ContainerProblemScoreCacheDao containerProblemScoreCacheDao;
    private final CourseChapterDao courseChapterDao;
    private final ProblemSetDao problemSetDao;
    private final ProblemSetProblemDao problemSetProblemDao;
    private final ProgrammingGradingDao programmingGradingDao;
    private final ProgrammingSubmissionDao programmingSubmissionDao;
    private final ChapterProblemDao chapterProblemDao;
    private final UserItemDao userItemDao;

    @Inject
    public BundleSubmissionServiceImpl(ArchiveDao archiveDao, BundleSubmissionDao bundleSubmissionDao, BundleGradingDao bundleGradingDao, BundleProblemGrader bundleProblemGrader, ContainerScoreCacheDao containerScoreCacheDao, ContainerProblemScoreCacheDao containerProblemScoreCacheDao, CourseChapterDao courseChapterDao, ProblemSetDao problemSetDao, ProblemSetProblemDao problemSetProblemDao, ProgrammingGradingDao programmingGradingDao, ProgrammingSubmissionDao programmingSubmissionDao, ChapterProblemDao chapterProblemDao, UserItemDao userItemDao) {
        super(bundleSubmissionDao, bundleGradingDao, bundleProblemGrader);
        this.archiveDao = archiveDao;
        this.bundleSubmissionDao = bundleSubmissionDao;
        this.bundleGradingDao = bundleGradingDao;
        this.containerScoreCacheDao = containerScoreCacheDao;
        this.containerProblemScoreCacheDao = containerProblemScoreCacheDao;
        this.courseChapterDao = courseChapterDao;
        this.problemSetDao = problemSetDao;
        this.problemSetProblemDao = problemSetProblemDao;
        this.programmingGradingDao = programmingGradingDao;
        this.programmingSubmissionDao = programmingSubmissionDao;
        this.chapterProblemDao = chapterProblemDao;
        this.userItemDao = userItemDao;
    }

    @Override
    public void afterGrade(String gradingJid, BundleAnswer answer) {
        BundleGradingModel bundleGradingModel = bundleGradingDao.findByJid(gradingJid);
        BundleSubmissionModel bundleSubmissionModel = bundleSubmissionDao.findByJid(bundleGradingModel.submissionJid);

        String userJid = bundleSubmissionModel.userCreate;
        String containerJid = bundleSubmissionModel.containerJid;
        String problemJid = bundleSubmissionModel.problemJid;

        BundleSubmission gradedBundleSubmission = findBundleSubmissionByJid(bundleSubmissionModel.jid);

        List<BundleSubmission> submissions = this.getBundleSubmissionsWithGradingsByContainerJidAndProblemJidAndUserJid(containerJid, problemJid, userJid);
        List<BundleSubmission> submissionsWithoutCurrent = submissions.stream().filter(s -> !s.getJid().equals(bundleSubmissionModel.jid)).collect(Collectors.toList());

        double newScore = JerahmeelSubmissionServiceUtils.countBundleSubmissionsMaxScore(submissions);
        if (containerProblemScoreCacheDao.existsByUserJidContainerJidAndProblemJid(userJid, containerJid, problemJid)) {
            JerahmeelSubmissionServiceUtils.updateContainerProblemScoreCache(containerProblemScoreCacheDao, userJid, containerJid, problemJid, newScore);
        } else {
            JerahmeelSubmissionServiceUtils.createContainerProblemScoreCache(containerProblemScoreCacheDao, userJid, containerJid, problemJid, newScore);
        }

        double previousScore = JerahmeelSubmissionServiceUtils.countBundleSubmissionsMaxScore(submissionsWithoutCurrent);
        if (gradedBundleSubmission.getGradings().size() > 1) {
            previousScore = Math.max(previousScore, gradedBundleSubmission.getGradings().get(gradedBundleSubmission.getGradings().size() - 2).getScore());
        }
        boolean scoreChanged = previousScore != newScore;
        double deltaScore = newScore - previousScore;

        if (containerJid.startsWith(JerahmeelJidUtils.PROBLEM_SET_JID_PREFIX)) {
            if (scoreChanged) {
                ProblemSetScoreCacheUtils.getInstance().updateProblemSetAndArchivesScoreCache(userJid, containerJid, deltaScore);
            }
            return;
        }

        if (scoreChanged) {
            ChapterScoreCacheUtils.getInstance().updateChapterAndCourseScoreCache(userJid, containerJid, deltaScore);
        }

        ChapterProgressCacheUtils.getInstance().updateChapterProblemProgressWithBundleSubmissions(userJid, containerJid, problemJid, submissions);
    }
}
