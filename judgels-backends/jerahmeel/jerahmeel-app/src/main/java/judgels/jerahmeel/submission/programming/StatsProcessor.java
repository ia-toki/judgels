package judgels.jerahmeel.submission.programming;

import java.util.Optional;
import javax.inject.Inject;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.TestCaseResult;
import judgels.gabriel.api.TestGroupResult;
import judgels.gabriel.api.Verdict;
import judgels.gabriel.api.Verdicts;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.CourseChapterDao;
import judgels.jerahmeel.persistence.CourseChapterModel;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.StatsUserChapterDao;
import judgels.jerahmeel.persistence.StatsUserChapterModel;
import judgels.jerahmeel.persistence.StatsUserCourseDao;
import judgels.jerahmeel.persistence.StatsUserCourseModel;
import judgels.jerahmeel.persistence.StatsUserDao;
import judgels.jerahmeel.persistence.StatsUserModel;
import judgels.jerahmeel.persistence.StatsUserProblemDao;
import judgels.jerahmeel.persistence.StatsUserProblemModel;
import judgels.jerahmeel.persistence.StatsUserProblemSetDao;
import judgels.jerahmeel.persistence.StatsUserProblemSetModel;
import judgels.jerahmeel.submission.SubmissionUtils;
import judgels.sandalphon.api.submission.programming.Grading;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.submission.programming.SubmissionConsumer;

public class StatsProcessor implements SubmissionConsumer {
    private final CourseChapterDao courseChapterDao;
    private final ChapterProblemDao chapterProblemDao;
    private final ProblemSetProblemDao problemSetProblemDao;

    private final StatsUserDao statsUserDao;
    private final StatsUserChapterDao statsUserChapterDao;
    private final StatsUserCourseDao statsUserCourseDao;
    private final StatsUserProblemDao statsUserProblemDao;
    private final StatsUserProblemSetDao statsUserProblemSetDao;

    @SuppressWarnings("checkstyle:visibilitymodifier")
    class ProblemStatsResult {
        int scoreDiff;
        boolean becomesAccepted;
    }

    @Inject
    public StatsProcessor(
            CourseChapterDao courseChapterDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            StatsUserDao statsUserDao,
            StatsUserChapterDao statsUserChapterDao,
            StatsUserCourseDao statsUserCourseDao,
            StatsUserProblemDao statsUserProblemDao,
            StatsUserProblemSetDao statsUserProblemSetDao) {

        this.courseChapterDao = courseChapterDao;
        this.chapterProblemDao = chapterProblemDao;
        this.problemSetProblemDao = problemSetProblemDao;
        this.statsUserDao = statsUserDao;
        this.statsUserChapterDao = statsUserChapterDao;
        this.statsUserCourseDao = statsUserCourseDao;
        this.statsUserProblemDao = statsUserProblemDao;
        this.statsUserProblemSetDao = statsUserProblemSetDao;
    }

    @Override
    public void accept(Submission submission) {
        if (SubmissionUtils.isChapter(submission.getContainerJid())) {
            ProblemStatsResult res = processProblemStats(submission);
            if (res == null) {
                return;
            }

            if (chapterProblemDao.selectByProblemJid(submission.getProblemJid()).isPresent()) {
                if (processChapterStats(submission, res.becomesAccepted)) {
                    processCourseStats(submission);
                }
                processUserStats(submission, res.scoreDiff);
            }
        } else {
            ProblemStatsResult res = processProblemStats(submission);
            if (res == null) {
                return;
            }

            if (problemSetProblemDao.selectByProblemJid(submission.getProblemJid()).isPresent()) {
                processProblemSetStats(submission, res.scoreDiff);
                processUserStats(submission, res.scoreDiff);
            }
        }
    }

    private ProblemStatsResult processProblemStats(Submission s) {
        if (!s.getLatestGrading().isPresent()) {
            return null;
        }
        Grading grading = s.getLatestGrading().get();
        if (!grading.getDetails().isPresent()) {
            return null;
        }
        GradingResultDetails details = grading.getDetails().get();

        int time = 0;
        int memory = 0;

        for (TestGroupResult groupResult : details.getTestDataResults()) {
            for (TestCaseResult caseResult : groupResult.getTestCaseResults()) {
                if (caseResult.getExecutionResult().isPresent()) {
                    SandboxExecutionResult executionResult = caseResult.getExecutionResult().get();
                    time = Math.max(time, executionResult.getTime());
                    memory = Math.max(memory, executionResult.getMemory());
                }
            }
        }

        boolean isAlreadyAccepted = false;
        boolean isNowAccepted = isAccepted(grading.getVerdict(), grading.getScore());

        Verdict verdict = isNowAccepted ? Verdict.ACCEPTED : grading.getVerdict();
        int scoreDiff = grading.getScore();

        Optional<StatsUserProblemModel> maybeModel =
                statsUserProblemDao.selectByUserJidAndProblemJid(s.getUserJid(), s.getProblemJid());

        if (maybeModel.isPresent()) {
            StatsUserProblemModel model = maybeModel.get();
            if (model.updatedAt.isAfter(s.getTime())) {
                return null;
            }
            model.submissionJid = s.getJid();

            scoreDiff = grading.getScore() - model.score;
            isAlreadyAccepted = isAccepted(Verdicts.fromCode(model.verdict), model.score);
            if (isAlreadyAccepted) {
                scoreDiff = Math.max(0, scoreDiff);
            }

            if (!isAlreadyAccepted || grading.getScore() >= model.score) {
                model.verdict = verdict.getCode();
                model.score = grading.getScore();
            }
            if (isNowAccepted) {
                model.time = time;
                model.memory = memory;
            }

            statsUserProblemDao.update(model);
        } else {
            StatsUserProblemModel model = new StatsUserProblemModel();

            model.userJid = s.getUserJid();
            model.problemJid = s.getProblemJid();
            model.submissionJid = s.getJid();
            model.verdict = verdict.getCode();
            model.score = grading.getScore();

            if (isNowAccepted) {
                model.time = time;
                model.memory = memory;
            }

            statsUserProblemDao.insert(model);
        }

        ProblemStatsResult result = new ProblemStatsResult();
        result.scoreDiff = scoreDiff;
        result.becomesAccepted = !isAlreadyAccepted && isNowAccepted;
        return result;
    }

    private void processUserStats(Submission s, int scoreDiff) {
        Optional<StatsUserModel> maybeModel = statsUserDao.selectByUserJid(s.getUserJid());
        if (maybeModel.isPresent()) {
            StatsUserModel model = maybeModel.get();
            model.score += scoreDiff;
            statsUserDao.update(model);
        } else {
            StatsUserModel model = new StatsUserModel();
            model.userJid = s.getUserJid();
            model.score = scoreDiff;
            statsUserDao.insert(model);
        }
    }

    private boolean processChapterStats(Submission s, boolean becomesAccepted) {
        if (!becomesAccepted) {
            return false;
        }

        Optional<StatsUserChapterModel> maybeModel =
                statsUserChapterDao.selectByUserJidAndChapterJid(s.getUserJid(), s.getContainerJid());

        int previousProgress = 0;
        if (maybeModel.isPresent()) {
            StatsUserChapterModel model = maybeModel.get();
            previousProgress = model.progress;
            model.progress++;
            statsUserChapterDao.update(model);
        } else {
            StatsUserChapterModel model = new StatsUserChapterModel();
            model.userJid = s.getUserJid();
            model.chapterJid = s.getContainerJid();
            model.progress = 1;
            statsUserChapterDao.insert(model);
        }

        int problemCount = chapterProblemDao.selectCountProgrammingByChapterJid(s.getContainerJid());
        return previousProgress + 1 == problemCount;
    }

    private void processCourseStats(Submission s) {
        Optional<CourseChapterModel> maybeCourseChapterModel = courseChapterDao.selectByChapterJid(s.getContainerJid());
        if (!maybeCourseChapterModel.isPresent()) {
            return;
        }
        String courseJid = maybeCourseChapterModel.get().courseJid;

        Optional<StatsUserCourseModel> maybeModel =
                statsUserCourseDao.selectByUserJidAndCourseJid(s.getUserJid(), courseJid);
        if (maybeModel.isPresent()) {
            StatsUserCourseModel model = maybeModel.get();
            model.progress++;
            statsUserCourseDao.update(model);
        } else {
            StatsUserCourseModel model = new StatsUserCourseModel();
            model.userJid = s.getUserJid();
            model.courseJid = courseJid;
            model.progress = 1;
            statsUserCourseDao.insert(model);
        }
    }

    private void processProblemSetStats(Submission s, int scoreDiff) {
        Optional<StatsUserProblemSetModel> maybeModel =
                statsUserProblemSetDao.selectByUserJidAndProblemSetJid(s.getUserJid(), s.getContainerJid());

        if (maybeModel.isPresent()) {
            StatsUserProblemSetModel model = maybeModel.get();
            model.score += scoreDiff;
            statsUserProblemSetDao.update(model);
        } else {
            StatsUserProblemSetModel model = new StatsUserProblemSetModel();
            model.userJid = s.getUserJid();
            model.problemSetJid = s.getContainerJid();
            model.score = scoreDiff;
            statsUserProblemSetDao.insert(model);
        }
    }

    private static boolean isAccepted(Verdict verdict, int score) {
        return verdict == Verdict.ACCEPTED || score >= 100;
    }
}
