package judgels.persistence.dao;

import jakarta.inject.Inject;
import judgels.persistence.DaoData;
import judgels.persistence.model.ContestBundleItemSubmissionModel;

public class ContestBundleItemSubmissionDao
        extends BaseBundleItemSubmissionDao<ContestBundleItemSubmissionModel> {

    @Inject
    public ContestBundleItemSubmissionDao(DaoData data) {
        super(data);
    }

    @Override
    public ContestBundleItemSubmissionModel createSubmissionModel() {
        return new ContestBundleItemSubmissionModel();
    }
}
