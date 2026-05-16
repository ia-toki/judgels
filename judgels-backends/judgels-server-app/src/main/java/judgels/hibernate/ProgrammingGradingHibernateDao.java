package judgels.hibernate;

import jakarta.inject.Inject;
import judgels.persistence.ProgrammingGradingDao;
import judgels.persistence.ProgrammingGradingModel;
import judgels.persistence.hibernate.HibernateDaoData;

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
