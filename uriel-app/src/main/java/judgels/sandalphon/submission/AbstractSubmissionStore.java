package judgels.sandalphon.submission;

import com.google.common.collect.Lists;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.submission.Grading;
import judgels.sandalphon.api.submission.Submission;
import judgels.sandalphon.persistence.AbstractGradingModel;
import judgels.sandalphon.persistence.AbstractSubmissionModel;
import judgels.sandalphon.persistence.BaseGradingDao;
import judgels.sandalphon.persistence.BaseSubmissionDao;

public abstract class AbstractSubmissionStore<SM extends AbstractSubmissionModel, GM extends AbstractGradingModel> {
    private final BaseSubmissionDao<SM> submissionDao;
    private final BaseGradingDao<GM> gradingDao;

    public AbstractSubmissionStore(BaseSubmissionDao<SM> submissionDao, BaseGradingDao<GM> gradingDao) {
        this.submissionDao = submissionDao;
        this.gradingDao = gradingDao;
    }

    public Page<Submission> getSubmissions(String containerJid, String userJid, SelectionOptions options) {
        Page<SM> submissionModels = submissionDao.selectPaged(containerJid, userJid, options);
        Set<String> submissionJids = submissionModels.getData().stream().map(m -> m.jid).collect(Collectors.toSet());
        Map<String, GM> gradingModels = gradingDao.selectAllLatestBySubmissionJids(submissionJids);

        return submissionModels.mapData(data ->
                Lists.transform(data, sm -> submissionFromModels(sm, gradingModels.get(sm.jid))));
    }

    public Submission submissionFromModels(SM model, GM gradingModel) {
        return new Submission.Builder()
                .id(model.id)
                .jid(model.jid)
                .userJid(model.createdBy)
                .problemJid(model.problemJid)
                .containerJid(model.containerJid)
                .gradingEngine(model.gradingEngine)
                .gradingLanguage(model.gradingLanguage)
                .time(model.createdAt)
                .latestGrading(gradingFromModel(gradingModel))
                .build();
    }

    public Grading gradingFromModel(GM model) {
        Grading.Builder grading = new Grading.Builder()
                .id(model.id)
                .jid(model.jid)
                .verdict(model.verdictCode)
                .score(model.score);

        return grading.build();
    }
}
