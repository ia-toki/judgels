package judgels.sandalphon.submission.bundle;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.bundle.Verdict;
import judgels.sandalphon.persistence.AbstractBundleItemSubmissionModel;
import judgels.sandalphon.persistence.BaseBundleItemSubmissionDao;

public class BaseItemSubmissionStore<M extends AbstractBundleItemSubmissionModel> implements ItemSubmissionStore {
    private final BaseBundleItemSubmissionDao<M> submissionDao;

    public BaseItemSubmissionStore(BaseBundleItemSubmissionDao<M> submissionDao) {
        this.submissionDao = submissionDao;
    }

    @Override
    public Optional<ItemSubmission> getSubmissionByJid(String submissionJid) {
        return submissionDao.selectByJid(submissionJid).map(this::fromModel);
    }

    @Override
    public Page<ItemSubmission> getSubmissions(
            String containerJid,
            Optional<String> createdBy,
            Optional<String> problemJid,
            Optional<Integer> page) {

        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        options.orderBy("updatedAt");
        options.orderDir(OrderDir.DESC);
        page.ifPresent(options::page);

        Page<M> submissionModels =
                submissionDao.selectPaged(containerJid, createdBy, problemJid, Optional.empty(), options.build());
        return submissionModels.mapPage(p -> Lists.transform(p, this::fromModel));
    }

    @Override
    public ItemSubmission upsertSubmission(
            String containerJid,
            String problemJid,
            String itemJid,
            String answer,
            Grading grading,
            String userJid) {

        Optional<M> maybeModel = submissionDao
                .selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(containerJid, problemJid, itemJid, userJid);

        if (maybeModel.isPresent()) {
            M model = maybeModel.get();
            model.answer = answer;
            model.verdict = grading.getVerdict().name();
            model.score = grading.getScore().orElse(null);
            return fromModel(submissionDao.update(model));
        } else {
            M model = submissionDao.createSubmissionModel();
            model.containerJid = containerJid;
            model.problemJid = problemJid;
            model.itemJid = itemJid;
            model.answer = answer;
            model.verdict = grading.getVerdict().name();
            model.score = grading.getScore().orElse(null);
            return fromModel(submissionDao.insert(model));
        }
    }

    @Override
    public void deleteSubmission(String containerJid, String problemJid, String itemJid, String userJid) {
        Optional<M> maybeModel = submissionDao
                .selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(containerJid, problemJid, itemJid, userJid);
        if (maybeModel.isPresent()) {
            submissionDao.delete(maybeModel.get());
        }
    }

    @Override
    public List<ItemSubmission> getLatestSubmissionsByUserInContainer(String containerJid, String userJid) {
        List<M> models =
                submissionDao.selectAllByContainerJidAndCreatedBy(containerJid, userJid);
        return models.stream()
                .map(this::fromModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemSubmission> getLatestSubmissionsByUserForProblemInContainer(
            String containerJid,
            String problemJid,
            String userJid) {

        List<M> models =
                submissionDao.selectAllByContainerJidAndProblemJidAndCreatedBy(containerJid, problemJid, userJid);
        return models.stream()
                .map(this::fromModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemSubmission> getSubmissionsForScoreboard(String containerJid) {
        List<M> models =
                submissionDao.selectAllByContainerJid(containerJid);
        return models.stream()
                .map(this::fromModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemSubmission> markSubmissionsForRegrade(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Integer> batchSize) {

        List<M> submissionModels = submissionDao.selectAllForRegrade(
                containerJid, problemJid, userJid);
        List<ItemSubmission> submissions = submissionModels.stream().map(this::fromModel).collect(Collectors.toList());

        int counter = 0;
        for (M submissionModel : submissionModels) {
            submissionModel.verdict = Verdict.PENDING_REGRADE.name();
            submissionModel.score = null;
            submissionDao.persist(submissionModel);

            // Occasionally flush to prevent out-of-memory errors due to Hibernate session cache overflowing
            counter++;
            if (counter > batchSize.orElse(50)) {
                submissionDao.flush();
                submissionDao.clear();
                counter = 0;
            }
        }

        return submissions;
    }

    @Override
    public void updateGrading(String submissionJid, Grading grading) {
        M model = submissionDao.selectByJid(submissionJid).get();
        model.verdict = grading.getVerdict().name();
        model.score = grading.getScore().orElse(null);
        submissionDao.persist(model);
    }

    private ItemSubmission fromModel(M model) {
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

