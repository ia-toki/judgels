package judgels.jerahmeel.submission.programming;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.TestCaseResult;
import judgels.gabriel.api.TestGroupResult;
import judgels.gabriel.api.Verdict;
import judgels.gabriel.api.Verdicts;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.ChapterProblemModel;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.jerahmeel.persistence.StatsUserDao;
import judgels.jerahmeel.persistence.StatsUserModel;
import judgels.jerahmeel.persistence.StatsUserProblemDao;
import judgels.jerahmeel.persistence.StatsUserProblemModel;
import judgels.sandalphon.api.submission.programming.Grading;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.submission.programming.SubmissionConsumer;

public class StatsProcessor implements SubmissionConsumer {
    private final ChapterProblemDao chapterProblemDao;
    private final ProblemSetProblemDao problemSetProblemDao;

    private final StatsUserDao statsUserDao;
    private final StatsUserProblemDao statsUserProblemDao;

    @SuppressWarnings("checkstyle:visibilitymodifier")
    static class ProblemStatsResult {
        int scoreDiff;
        boolean becomesAccepted;
    }

    @Inject
    public StatsProcessor(
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            StatsUserDao statsUserDao,
            StatsUserProblemDao statsUserProblemDao) {

        this.chapterProblemDao = chapterProblemDao;
        this.problemSetProblemDao = problemSetProblemDao;
        this.statsUserDao = statsUserDao;
        this.statsUserProblemDao = statsUserProblemDao;
    }

    @Override
    @UnitOfWork
    public void accept(Submission submission) {
        Optional<ChapterProblemModel> cm = chapterProblemDao.selectByProblemJid(submission.getProblemJid());
        List<ProblemSetProblemModel> pms = problemSetProblemDao.selectAllByProblemJid(submission.getProblemJid());

        if (!cm.isPresent() && pms.isEmpty()) {
            return;
        }

        ProblemStatsResult res = processProblemStats(submission);
        if (res == null) {
            return;
        }

        processUserStats(submission, res.scoreDiff);
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

    private static boolean isAccepted(Verdict verdict, int score) {
        return verdict == Verdict.ACCEPTED || score >= 100;
    }
}
