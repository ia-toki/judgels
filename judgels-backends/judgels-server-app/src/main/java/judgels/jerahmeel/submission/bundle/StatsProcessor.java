package judgels.jerahmeel.submission.bundle;

import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import judgels.gabriel.api.Verdict;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.ChapterProblemModel;
import judgels.jerahmeel.persistence.StatsUserProblemDao;
import judgels.jerahmeel.persistence.StatsUserProblemModel;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.submission.bundle.ItemSubmissionConsumer;

public class StatsProcessor implements ItemSubmissionConsumer {
    private final ChapterProblemDao chapterProblemDao;
    private final StatsUserProblemDao statsUserProblemDao;

    @Inject
    public StatsProcessor(
            ChapterProblemDao chapterProblemDao,
            StatsUserProblemDao statsUserProblemDao) {

        this.chapterProblemDao = chapterProblemDao;
        this.statsUserProblemDao = statsUserProblemDao;
    }

    @Override
    public void accept(ItemSubmission s, Map<String, Optional<Grading>> itemGradingsMap) {
        Optional<ChapterProblemModel> cm = chapterProblemDao.selectByProblemJid(s.getProblemJid());
        if (!cm.isPresent()) {
            return;
        }

        Optional<StatsUserProblemModel> maybeModel = statsUserProblemDao.selectByUserJidAndProblemJid(s.getUserJid(), s.getProblemJid());

        if (maybeModel.isPresent()) {
            StatsUserProblemModel model = maybeModel.get();
            if (model.verdict.equals(Verdict.ACCEPTED.getCode())) {
                return;
            }
        }

        Verdict newVerdict = Verdict.ACCEPTED;
        for (Optional<Grading> grading : itemGradingsMap.values()) {
            if (grading.isEmpty() || grading.get().getVerdict() != judgels.sandalphon.api.submission.bundle.Verdict.ACCEPTED) {
                newVerdict = Verdict.WRONG_ANSWER;
                break;
            }
        }

        if (maybeModel.isPresent()) {
            StatsUserProblemModel model = maybeModel.get();
            model.submissionJid = s.getJid();
            model.verdict = newVerdict.getCode();
            statsUserProblemDao.update(model);
        } else {
            StatsUserProblemModel model = new StatsUserProblemModel();
            model.userJid = s.getUserJid();
            model.problemJid = s.getProblemJid();
            model.submissionJid = s.getJid();
            model.verdict = newVerdict.getCode();
            model.score = 0;
            statsUserProblemDao.insert(model);
        }
    }
}
