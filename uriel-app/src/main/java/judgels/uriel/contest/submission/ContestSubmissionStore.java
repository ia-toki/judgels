package judgels.uriel.contest.submission;

import javax.inject.Inject;
import judgels.sandalphon.submission.AbstractSubmissionStore;
import judgels.uriel.persistence.ContestSubmissionDao;
import judgels.uriel.persistence.ContestSubmissionModel;

public class ContestSubmissionStore extends AbstractSubmissionStore<ContestSubmissionModel> {
    @Inject
    public ContestSubmissionStore(ContestSubmissionDao submissionDao) {
        super(submissionDao);
    }
}
