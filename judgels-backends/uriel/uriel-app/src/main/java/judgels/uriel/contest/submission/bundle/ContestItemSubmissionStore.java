package judgels.uriel.contest.submission.bundle;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
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

    public ItemSubmission upsertSubmission(ContestItemSubmissionData data, String userJid) {
        Optional<ContestBundleItemSubmissionModel> maybeModel = submissionDao
                .selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(
                        data.getContestJid(), data.getProblemJid(), data.getItemJid(), userJid);

        if (maybeModel.isPresent()) {
            ContestBundleItemSubmissionModel model = maybeModel.get();
            model.answer = data.getAnswer();
            return fromModel(submissionDao.update(model));
        } else {
            ContestBundleItemSubmissionModel model = new ContestBundleItemSubmissionModel();
            model.containerJid = data.getContestJid();
            model.problemJid = data.getProblemJid();
            model.itemJid = data.getItemJid();
            model.answer = data.getAnswer();
            return fromModel(submissionDao.insert(model));
        }
    }

    public Map<String, ItemSubmission> getLatestSubmissionsByUserForProblemInContest(
            String containerJid,
            String problemJid,
            String userJid) {

        List<ContestBundleItemSubmissionModel>
                models = submissionDao.selectByContainerJidAndProblemJidAndCreatedBy(containerJid, problemJid, userJid);
        return models.stream()
                .map(ContestItemSubmissionStore::fromModel)
                .collect(Collectors.toMap(v -> v.getItemJid(), Function.identity()));
    }

    public Page<ItemSubmission> getSubmissions(
            String containerJid,
            Optional<String> createdBy,
            Optional<String> problemJid,
            Optional<Integer> page) {

        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(options::page);

        Page<ContestBundleItemSubmissionModel> submissionModels =
                submissionDao.selectPaged(containerJid, createdBy, problemJid, Optional.empty(), options.build());
        return submissionModels.mapPage(p -> Lists.transform(p, ContestItemSubmissionStore::fromModel));
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
                .build();
    }
}
