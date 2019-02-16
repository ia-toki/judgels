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
import judgels.sandalphon.api.submission.BundleSubmission;
import judgels.uriel.api.contest.submission.bundle.ContestBundleSubmissionData;
import judgels.uriel.persistence.ContestBundleSubmissionDao;
import judgels.uriel.persistence.ContestBundleSubmissionModel;

@Singleton
public class ContestBundleSubmissionStore {
    private final ContestBundleSubmissionDao submissionDao;

    @Inject
    public ContestBundleSubmissionStore(ContestBundleSubmissionDao submissionDao) {
        this.submissionDao = submissionDao;
    }

    public BundleSubmission upsertSubmission(ContestBundleSubmissionData data, String userJid) {
        Optional<ContestBundleSubmissionModel> maybeModel = submissionDao
                .selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(
                        data.getContestJid(), data.getProblemJid(), data.getItemJid(), userJid);

        if (maybeModel.isPresent()) {
            ContestBundleSubmissionModel model = maybeModel.get();
            model.value = data.getValue();
            return fromModel(submissionDao.update(model));
        } else {
            ContestBundleSubmissionModel model = new ContestBundleSubmissionModel();
            model.containerJid = data.getContestJid();
            model.problemJid = data.getProblemJid();
            model.itemJid = data.getItemJid();
            model.value = data.getValue();
            return fromModel(submissionDao.insert(model));
        }
    }

    public Map<String, BundleSubmission> getLatestSubmissionsByUserForProblemInContest(
            String containerJid,
            String problemJid,
            String userJid) {

        List<ContestBundleSubmissionModel>
                models = submissionDao.selectByContainerJidAndProblemJidAndCreatedBy(containerJid, problemJid, userJid);
        return models.stream()
                .map(ContestBundleSubmissionStore::fromModel)
                .collect(Collectors.toMap(v -> v.getItemJid(), Function.identity()));
    }

    public Page<BundleSubmission> getSubmissions(
            String containerJid,
            Optional<String> createdBy,
            Optional<String> problemJid,
            Optional<Integer> page) {

        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(options::page);

        Page<ContestBundleSubmissionModel> submissionModels =
                submissionDao.selectPaged(containerJid, createdBy, problemJid, Optional.empty(), options.build());
        return submissionModels.mapPage(p -> Lists.transform(p, ContestBundleSubmissionStore::fromModel));
    }

    private static BundleSubmission fromModel(ContestBundleSubmissionModel model) {
        return new BundleSubmission.Builder()
                .id(model.id)
                .jid(model.jid)
                .containerJid(model.containerJid)
                .problemJid(model.problemJid)
                .itemJid(model.itemJid)
                .value(model.value)
                .userJid(model.updatedBy)
                .time(model.updatedAt)
                .build();
    }
}
