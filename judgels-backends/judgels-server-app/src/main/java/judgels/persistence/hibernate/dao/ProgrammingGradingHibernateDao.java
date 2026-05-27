package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import judgels.persistence.dao.ProgrammingGradingDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.model.ProgrammingGradingModel;

public class ProgrammingGradingHibernateDao
        extends AbstractProgrammingGradingHibernateDao<ProgrammingGradingModel>
        implements ProgrammingGradingDao {

    @Inject
    public ProgrammingGradingHibernateDao(HibernateDaoData data) {
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
