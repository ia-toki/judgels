package org.iatoki.judgels.jerahmeel.submission.programming;

import org.iatoki.judgels.api.sealtiel.SealtielClientAPI;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.jerahmeel.jid.JerahmeelJidUtils;
import org.iatoki.judgels.jerahmeel.archive.ArchiveDao;
import org.iatoki.judgels.jerahmeel.chapter.ChapterProgressCacheUtils;
import org.iatoki.judgels.jerahmeel.chapter.ChapterScoreCacheUtils;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemDao;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterDao;
import org.iatoki.judgels.jerahmeel.grading.bundle.BundleGradingDao;
import org.iatoki.judgels.jerahmeel.grading.programming.ProgrammingGradingDao;
import org.iatoki.judgels.jerahmeel.grading.programming.ProgrammingGradingModel;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetDao;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblemDao;
import org.iatoki.judgels.jerahmeel.scorecache.ContainerProblemScoreCacheDao;
import org.iatoki.judgels.jerahmeel.scorecache.ContainerScoreCacheDao;
import org.iatoki.judgels.jerahmeel.scorecache.ProblemSetScoreCacheUtils;
import org.iatoki.judgels.jerahmeel.submission.JerahmeelSubmissionServiceUtils;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionDao;
import org.iatoki.judgels.jerahmeel.user.item.UserItemDao;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmission;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;
import org.iatoki.judgels.sandalphon.problem.programming.submission.AbstractProgrammingSubmissionServiceImpl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public final class ProgrammingSubmissionServiceImpl extends AbstractProgrammingSubmissionServiceImpl<ProgrammingSubmissionModel, ProgrammingGradingModel> implements ProgrammingSubmissionService {

    private final ArchiveDao archiveDao;
    private final BundleSubmissionDao bundleSubmissionDao;
    private final BundleGradingDao bundleGradingDao;
    private final ContainerScoreCacheDao containerScoreCacheDao;
    private final ContainerProblemScoreCacheDao containerProblemScoreCacheDao;
    private final CourseChapterDao courseChapterDao;
    private final ProblemSetDao problemSetDao;
    private final ProblemSetProblemDao problemSetProblemDao;
    private final ProgrammingSubmissionDao programmingSubmissionDao;
    private final ProgrammingGradingDao programmingGradingDao;
    private final ChapterProblemDao chapterProblemDao;
    private final UserItemDao userItemDao;

    @Inject
    public ProgrammingSubmissionServiceImpl(ArchiveDao archiveDao, BundleSubmissionDao bundleSubmissionDao, BundleGradingDao bundleGradingDao, ContainerScoreCacheDao containerScoreCacheDao, ContainerProblemScoreCacheDao containerProblemScoreCacheDao, CourseChapterDao courseChapterDao, ProblemSetDao problemSetDao, ProblemSetProblemDao problemSetProblemDao, ProgrammingSubmissionDao programmingSubmissionDao, ProgrammingGradingDao programmingGradingDao, SealtielClientAPI sealtielClientAPI, @GabrielClientJid String gabrielClientJid, ChapterProblemDao chapterProblemDao, UserItemDao userItemDao) {
        super(programmingSubmissionDao, programmingGradingDao, sealtielClientAPI, gabrielClientJid);
        this.archiveDao = archiveDao;
        this.bundleSubmissionDao = bundleSubmissionDao;
        this.bundleGradingDao = bundleGradingDao;
        this.containerScoreCacheDao = containerScoreCacheDao;
        this.containerProblemScoreCacheDao = containerProblemScoreCacheDao;
        this.courseChapterDao = courseChapterDao;
        this.problemSetDao = problemSetDao;
        this.problemSetProblemDao = problemSetProblemDao;
        this.programmingSubmissionDao = programmingSubmissionDao;
        this.programmingGradingDao = programmingGradingDao;
        this.chapterProblemDao = chapterProblemDao;
        this.userItemDao = userItemDao;
    }

    @Override
    public void afterGrade(String gradingJid, GradingResult result) {
        ProgrammingGradingModel programmingGradingModel = programmingGradingDao.findByJid(gradingJid);
        ProgrammingSubmissionModel programmingSubmissionModel = programmingSubmissionDao.findByJid(programmingGradingModel.submissionJid);

        String userJid = programmingSubmissionModel.userCreate;
        String containerJid = programmingSubmissionModel.containerJid;
        String problemJid = programmingSubmissionModel.problemJid;

        ProgrammingSubmission gradedProgrammingSubmission = this.findProgrammingSubmissionByJid(programmingSubmissionModel.jid);

        List<ProgrammingSubmission> submissions = this.getProgrammingSubmissionsWithGradingsByContainerJidAndProblemJidAndUserJid(containerJid, problemJid, userJid);
        List<ProgrammingSubmission> submissionsWithoutCurrent = submissions.stream().filter(s -> !s.getJid().equals(programmingSubmissionModel.jid)).collect(Collectors.toList());

        double newScore = JerahmeelSubmissionServiceUtils.countProgrammingSubmissionsMaxScore(submissions);
        if (containerProblemScoreCacheDao.existsByUserJidContainerJidAndProblemJid(userJid, containerJid, problemJid)) {
            JerahmeelSubmissionServiceUtils.updateContainerProblemScoreCache(containerProblemScoreCacheDao, userJid, containerJid, problemJid, newScore);
        } else {
            JerahmeelSubmissionServiceUtils.createContainerProblemScoreCache(containerProblemScoreCacheDao, userJid, containerJid, problemJid, newScore);
        }

        double previousScore = JerahmeelSubmissionServiceUtils.countProgrammingSubmissionsMaxScore(submissionsWithoutCurrent);
        if (gradedProgrammingSubmission.getGradings().size() > 1) {
            previousScore = Math.max(previousScore, gradedProgrammingSubmission.getGradings().get(gradedProgrammingSubmission.getGradings().size() - 2).getScore());
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

        ChapterProgressCacheUtils.getInstance().updateChapterProblemProgressWithProgrammingSubmissions(userJid, containerJid, problemJid, submissions);
    }
}
