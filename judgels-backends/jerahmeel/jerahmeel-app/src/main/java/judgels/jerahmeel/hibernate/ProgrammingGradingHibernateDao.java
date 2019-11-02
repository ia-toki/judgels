package judgels.jerahmeel.hibernate;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jerahmeel.persistence.ProgrammingGradingDao;
import judgels.jerahmeel.persistence.ProgrammingGradingModel;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.hibernate.AbstractProgrammingGradingHibernateDao;

@Singleton
public class ProgrammingGradingHibernateDao extends AbstractProgrammingGradingHibernateDao<
        ProgrammingGradingModel> implements ProgrammingGradingDao {

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
