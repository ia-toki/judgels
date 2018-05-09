package judgels.uriel.contest.submission;

import javax.inject.Inject;
import judgels.sandalphon.submission.AbstractSubmissionStore;
import judgels.uriel.persistence.ContestGradingDao;
import judgels.uriel.persistence.ContestGradingModel;
import judgels.uriel.persistence.ContestSubmissionDao;
import judgels.uriel.persistence.ContestSubmissionModel;

public class ContestSubmissionStore extends AbstractSubmissionStore<ContestSubmissionModel, ContestGradingModel> {
    @Inject
    public ContestSubmissionStore(ContestSubmissionDao submissionDao, ContestGradingDao gradingDao) {
        super(submissionDao, gradingDao);
    }
}
