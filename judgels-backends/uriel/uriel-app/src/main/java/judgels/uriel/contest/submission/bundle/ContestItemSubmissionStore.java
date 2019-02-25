package judgels.uriel.contest.submission.bundle;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.bundle.Verdict;
import judgels.uriel.api.contest.submission.bundle.ContestItemSubmissionData;
import judgels.uriel.persistence.ContestBundleItemSubmissionDao;
import judgels.uriel.persistence.ContestBundleItemSubmissionModel;

@Singleton
public class ContestItemSubmissionStore {
    private final ContestBundleItemSubmissionDao submissionDao;

    @Inject
    public ContestItemSubmissionStore(ContestBundleItemSubmissionDao submissionDao) {
        this.submissionDao = submissionDao;
    }

    public ItemSubmission upsertSubmission(
            ContestItemSubmissionData data,
            Grading grading,
            String userJid) {

        Optional<ContestBundleItemSubmissionModel> maybeModel = submissionDao
                .selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(
                        data.getContestJid(), data.getProblemJid(), data.getItemJid(), userJid);

        if (maybeModel.isPresent()) {
            ContestBundleItemSubmissionModel model = maybeModel.get();
            model.answer = data.getAnswer();
            model.verdict = grading.getVerdict().name();
            model.score = grading.getScore().orElse(null);
            return fromModel(submissionDao.update(model));
        } else {
            ContestBundleItemSubmissionModel model = new ContestBundleItemSubmissionModel();
            model.containerJid = data.getContestJid();
            model.problemJid = data.getProblemJid();
            model.itemJid = data.getItemJid();
            model.answer = data.getAnswer();
            model.verdict = grading.getVerdict().name();
            model.score = grading.getScore().orElse(null);
            return fromModel(submissionDao.insert(model));
        }
    }

    public List<ItemSubmission> getLatestSubmissionsByUserInContest(String containerJid, String userJid) {
        List<ContestBundleItemSubmissionModel>
                models = submissionDao.selectByContainerJidAndCreatedBy(containerJid, userJid);
        return models.stream()
                .map(ContestItemSubmissionStore::fromModel)
                .collect(Collectors.toList());
    }

    public List<ItemSubmission> getLatestSubmissionsByUserForProblemInContest(
            String containerJid,
            String problemJid,
            String userJid) {

        List<ContestBundleItemSubmissionModel>
                models = submissionDao.selectByContainerJidAndProblemJidAndCreatedBy(containerJid, problemJid, userJid);
        return models.stream()
                .map(ContestItemSubmissionStore::fromModel)
                .collect(Collectors.toList());
    }

    private static ItemSubmission fromModel(ContestBundleItemSubmissionModel model) {
        return new ItemSubmission.Builder()
                .id(model.id)
                .jid(model.jid)
                .containerJid(model.containerJid)
                .problemJid(model.problemJid)
                .itemJid(model.itemJid)
                .answer(model.answer)
                .userJid(model.updatedBy)
                .time(model.updatedAt)
                .grading(new Grading.Builder()
                    .verdict(Verdict.valueOf(model.verdict))
                    .score(Optional.ofNullable(model.score))
                    .build())
                .build();
    }
}
