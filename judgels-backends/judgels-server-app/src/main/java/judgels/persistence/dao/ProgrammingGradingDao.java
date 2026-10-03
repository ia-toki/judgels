package judgels.persistence.dao;

import jakarta.inject.Inject;
import judgels.persistence.DaoData;
import judgels.persistence.model.ProgrammingGradingModel;

public class ProgrammingGradingDao
        extends BaseProgrammingGradingDao<ProgrammingGradingModel> {

    @Inject
    public ProgrammingGradingDao(DaoData data) {
        super(data);
    }

    @Override
    public ProgrammingGradingModel createGradingModel() {
        return new ProgrammingGradingModel();
    }

    @Override
    public Class<ProgrammingGradingModel> getGradingModelClass() {
        return ProgrammingGradingModel.class;
    }
}
