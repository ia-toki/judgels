package judgels.uriel.contest.submission.bundle;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.bundle.Verdict;
import judgels.uriel.persistence.ContestBundleItemSubmissionDao;
import judgels.uriel.persistence.ContestBundleItemSubmissionModel;

@Singleton
public class ContestItemSubmissionStore {
    private final ContestBundleItemSubmissionDao submissionDao;

    @Inject
    public ContestItemSubmissionStore(ContestBundleItemSubmissionDao submissionDao) {
        this.submissionDao = submissionDao;
    }

    public Page<ItemSubmission> getSubmissions(
            String containerJid,
            Optional<String> createdBy,
            Optional<String> problemJid,
            Optional<Integer> page) {

        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        options.orderBy("updatedAt");
        options.orderDir(OrderDir.DESC);
        page.ifPresent(options::page);

        Page<ContestBundleItemSubmissionModel> submissionModels =
                submissionDao.selectPaged(containerJid, createdBy, problemJid, Optional.empty(), options.build());
        return submissionModels.mapPage(p -> Lists.transform(p, ContestItemSubmissionStore::fromModel));
    }

    public ItemSubmission upsertSubmission(
            String contestJid,
            String problemJid,
            String itemJid,
            String answer,
            Grading grading,
            String userJid) {

        Optional<ContestBundleItemSubmissionModel> maybeModel = submissionDao
                .selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(contestJid, problemJid, itemJid, userJid);

        if (maybeModel.isPresent()) {
            ContestBundleItemSubmissionModel model = maybeModel.get();
            model.answer = answer;
            model.verdict = grading.getVerdict().name();
            model.score = grading.getScore().orElse(null);
            return fromModel(submissionDao.update(model));
        } else {
            ContestBundleItemSubmissionModel model = new ContestBundleItemSubmissionModel();
            model.containerJid = contestJid;
            model.problemJid = problemJid;
            model.itemJid = itemJid;
            model.answer = answer;
            model.verdict = grading.getVerdict().name();
            model.score = grading.getScore().orElse(null);
            return fromModel(submissionDao.insert(model));
        }
    }

    public void deleteSubmission(String contestJid, String problemJid, String itemJid, String userJid) {
        Optional<ContestBundleItemSubmissionModel> maybeModel = submissionDao
                .selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(contestJid, problemJid, itemJid, userJid);
        if (maybeModel.isPresent()) {
            submissionDao.delete(maybeModel.get());
        }
    }

    public List<ItemSubmission> getLatestSubmissionsByUserInContest(String containerJid, String userJid) {
        List<ContestBundleItemSubmissionModel> models =
                submissionDao.selectAllByContainerJidAndCreatedBy(containerJid, userJid);
        return models.stream()
                .map(ContestItemSubmissionStore::fromModel)
                .collect(Collectors.toList());
    }

    public List<ItemSubmission> getLatestSubmissionsByUserForProblemInContest(
            String containerJid,
            String problemJid,
            String userJid) {

        List<ContestBundleItemSubmissionModel> models =
                submissionDao.selectAllByContainerJidAndProblemJidAndCreatedBy(containerJid, problemJid, userJid);
        return models.stream()
                .map(ContestItemSubmissionStore::fromModel)
                .collect(Collectors.toList());
    }

    public List<ItemSubmission> getSubmissionsForScoreboard(String containerJid) {
        List<ContestBundleItemSubmissionModel> models =
                submissionDao.selectAllByContainerJid(containerJid);
        return models.stream()
                .map(ContestItemSubmissionStore::fromModel)
                .collect(Collectors.toList());
    }

    private static ItemSubmission fromModel(ContestBundleItemSubmissionModel model) {
        return new ItemSubmission.Builder()
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
