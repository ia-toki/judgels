package judgels.uriel.hibernate;

import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.sandalphon.hibernate.AbstractGradingHibernateDao;
import judgels.uriel.persistence.ContestGradingDao;
import judgels.uriel.persistence.ContestGradingModel;
import org.hibernate.SessionFactory;

@Singleton
public class ContestGradingHibernateDao extends AbstractGradingHibernateDao<ContestGradingModel>
        implements ContestGradingDao {

    @Inject
    public ContestGradingHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public ContestGradingModel createGradingModel() {
        return new ContestGradingModel();
    }

    @Override
    public Class<ContestGradingModel> getGradingModelClass() {
        return ContestGradingModel.class;
    }
}
