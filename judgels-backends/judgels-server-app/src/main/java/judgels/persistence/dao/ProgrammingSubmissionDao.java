package judgels.persistence.dao;

import jakarta.inject.Inject;
import judgels.persistence.DaoData;
import judgels.persistence.model.ProgrammingSubmissionModel;

public class ProgrammingSubmissionDao
        extends BaseProgrammingSubmissionDao<ProgrammingSubmissionModel> {

    @Inject
    public ProgrammingSubmissionDao(DaoData data) {
        super(data);
    }

    @Override
    public ProgrammingSubmissionModel createSubmissionModel() {
        return new ProgrammingSubmissionModel();
    }
}
