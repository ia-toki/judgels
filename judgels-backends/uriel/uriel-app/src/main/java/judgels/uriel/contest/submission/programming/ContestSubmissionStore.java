package judgels.uriel.contest.submission.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import judgels.sandalphon.submission.programming.AbstractSubmissionStore;
import judgels.uriel.persistence.ContestGradingDao;
import judgels.uriel.persistence.ContestProgrammingGradingModel;
import judgels.uriel.persistence.ContestProgrammingSubmissionDao;
import judgels.uriel.persistence.ContestProgrammingSubmissionModel;

public class ContestSubmissionStore
        extends AbstractSubmissionStore<ContestProgrammingSubmissionModel, ContestProgrammingGradingModel> {

    @Inject
    public ContestSubmissionStore(
            ContestProgrammingSubmissionDao submissionDao,
            ContestGradingDao gradingDao,
            ObjectMapper mapper) {

        super(submissionDao, gradingDao, mapper);
    }
}
