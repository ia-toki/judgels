package judgels.uriel.contest.submission.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import judgels.sandalphon.submission.AbstractSubmissionStore;
import judgels.uriel.persistence.ContestGradingDao;
import judgels.uriel.persistence.ContestGradingModel;
import judgels.uriel.persistence.ContestProgrammingSubmissionDao;
import judgels.uriel.persistence.ContestProgrammingSubmissionModel;

public class ContestProgrammingSubmissionStore
        extends AbstractSubmissionStore<ContestProgrammingSubmissionModel, ContestGradingModel> {
    @Inject
    public ContestProgrammingSubmissionStore(
            ContestProgrammingSubmissionDao submissionDao,
            ContestGradingDao gradingDao,
            ObjectMapper mapper) {

        super(submissionDao, gradingDao, mapper);
    }
}
