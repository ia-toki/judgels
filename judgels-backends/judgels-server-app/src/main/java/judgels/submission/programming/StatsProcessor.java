package judgels.submission.programming;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import judgels.api.submission.programming.Grading;
import judgels.api.submission.programming.Submission;
import judgels.grading.api.GradingResultDetails;
import judgels.grading.api.SandboxExecutionResult;
import judgels.grading.api.TestCaseResult;
import judgels.grading.api.TestGroupResult;
import judgels.grading.api.Verdict;
import judgels.grading.api.Verdicts;
import judgels.persistence.ChapterProblemDao;
import judgels.persistence.ChapterProblemModel;
import judgels.persistence.ProblemSetProblemDao;
import judgels.persistence.ProblemSetProblemModel;
import judgels.persistence.StatsUserDao;
import judgels.persistence.StatsUserModel;
import judgels.persistence.StatsUserProblemDao;
import judgels.persistence.StatsUserProblemModel;

public class StatsProcessor implements SubmissionConsumer {
    private final ChapterProblemDao chapterProblemDao;
    private final ProblemSetProblemDao problemSetProblemDao;

    private final StatsUserDao statsUserDao;
    private final StatsUserProblemDao statsUserProblemDao;

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

        if (processProblemStats(submission)) {
            processUserStats(submission);
        }
    }

    private boolean processProblemStats(Submission s) {
        if (!s.getLatestGrading().isPresent()) {
            return false;
        }
        Grading grading = s.getLatestGrading().get();
        if (!grading.getDetails().isPresent()) {
            return false;
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

        Optional<StatsUserProblemModel> maybeModel =
                statsUserProblemDao.selectByUserJidAndProblemJid(s.getUserJid(), s.getProblemJid());

        if (maybeModel.isPresent()) {
            StatsUserProblemModel model = maybeModel.get();
            model.submissionJid = s.getJid();

            isAlreadyAccepted = isAccepted(Verdicts.fromCode(model.verdict), model.score);

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

        return true;
    }

    private void processUserStats(Submission s) {
        int totalScore = statsUserProblemDao.selectTotalScoreByUserJid(s.getUserJid());

        Optional<StatsUserModel> maybeModel = statsUserDao.selectByUserJid(s.getUserJid());
        if (maybeModel.isPresent()) {
            StatsUserModel model = maybeModel.get();
            model.score = totalScore;
            statsUserDao.update(model);
        } else {
            StatsUserModel model = new StatsUserModel();
            model.userJid = s.getUserJid();
            model.score = totalScore;
            statsUserDao.insert(model);
        }
    }

    private static boolean isAccepted(Verdict verdict, int score) {
        return verdict == Verdict.ACCEPTED || score >= 100;
    }
}
