package judgels.sandalphon.submission.bundle;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import judgels.persistence.Model_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.bundle.Verdict;
import judgels.sandalphon.persistence.AbstractBundleItemSubmissionModel;
import judgels.sandalphon.persistence.AbstractBundleItemSubmissionModel_;
import judgels.sandalphon.persistence.BaseBundleItemSubmissionDao;
import judgels.sandalphon.persistence.BaseBundleItemSubmissionDao.BaseBundleItemSubmissionQueryBuilder;

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
            int pageNumber,
            int pageSize) {

        BaseBundleItemSubmissionQueryBuilder<M> query = submissionDao.select()
                .whereContainerIs(containerJid);

        if (createdBy.isPresent()) {
            query.whereAuthorIs(createdBy.get());
        }
        if (problemJid.isPresent()) {
            query.whereProblemIs(problemJid.get());
        }

        return query
                .orderBy(Model_.UPDATED_AT, OrderDir.DESC)
                .paged(pageNumber, pageSize)
                .mapPage(p -> Lists.transform(p, this::fromModel));
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
                .select()
                .whereContainerIs(containerJid)
                .whereProblemIs(problemJid)
                .whereItemIs(itemJid)
                .whereAuthorIs(userJid)
                .unique();

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
        submissionDao
                .select()
                .whereContainerIs(containerJid)
                .whereProblemIs(problemJid)
                .whereItemIs(itemJid)
                .whereAuthorIs(userJid)
                .unique()
                .ifPresent(submissionDao::delete);
    }

    @Override
    public List<ItemSubmission> getLatestSubmissionsByUserInContainer(String containerJid, String userJid) {
        return Lists.transform(submissionDao
                .select()
                .whereContainerIs(containerJid)
                .whereAuthorIs(userJid)
                .all(), this::fromModel);
    }

    @Override
    public List<ItemSubmission> getLatestSubmissionsByUserForProblemInContainer(
            String containerJid,
            String problemJid,
            String userJid) {

        return Lists.transform(submissionDao
                .select()
                .whereContainerIs(containerJid)
                .whereProblemIs(problemJid)
                .whereAuthorIs(userJid)
                .all(), this::fromModel);
    }

    @Override
    public List<ItemSubmission> getSubmissionsForScoreboard(String containerJid) {
        return Lists.transform(submissionDao
                .select()
                .whereContainerIs(containerJid)
                .all(), this::fromModel);
    }

    @Override
    public List<ItemSubmission> getSubmissionsForRegrade(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid) {

        BaseBundleItemSubmissionQueryBuilder<M> query = submissionDao.select();

        if (containerJid.isPresent()) {
            query.whereContainerIs(containerJid.get());
        }
        if (userJid.isPresent()) {
            query.whereAuthorIs(userJid.get());
        }
        if (problemJid.isPresent()) {
            query.whereProblemIs(problemJid.get());
        }

        List<M> submissionModels = query
                .orderBy(AbstractBundleItemSubmissionModel_.PROBLEM_JID, OrderDir.ASC)
                .all();
        return Lists.transform(submissionModels, this::fromModel);
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
