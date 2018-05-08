package judgels.sandalphon.submission;

import com.google.common.collect.Lists;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.submission.Submission;
import judgels.sandalphon.persistence.AbstractSubmissionModel;
import judgels.sandalphon.persistence.BaseSubmissionDao;

public abstract class AbstractSubmissionStore<SM extends AbstractSubmissionModel> {
    private final BaseSubmissionDao<SM> submissionDao;

    public AbstractSubmissionStore(BaseSubmissionDao<SM> submissionDao) {
        this.submissionDao = submissionDao;
    }

    public Page<Submission> getSubmissions(String containerJid, String userJid, SelectionOptions options) {
        return submissionDao.selectPaged(containerJid, userJid, options)
                .mapData(data -> Lists.transform(data, this::fromModel));
    }

    public Submission fromModel(SM model) {
        return new Submission.Builder()
                .id(model.id)
                .jid(model.jid)
                .userJid(model.createdBy)
                .problemJid(model.problemJid)
                .containerJid(model.containerJid)
                .gradingEngine(model.gradingEngine)
                .gradingLanguage(model.gradingLanguage)
                .time(model.createdAt)
                .build();
    }
}
