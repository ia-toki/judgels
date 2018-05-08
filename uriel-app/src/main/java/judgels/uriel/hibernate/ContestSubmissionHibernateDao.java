package judgels.uriel.hibernate;

import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.sandalphon.hibernate.AbstractSubmissionHibernateDao;
import judgels.uriel.persistence.ContestSubmissionDao;
import judgels.uriel.persistence.ContestSubmissionModel;
import org.hibernate.SessionFactory;

@Singleton
public class ContestSubmissionHibernateDao extends AbstractSubmissionHibernateDao<ContestSubmissionModel>
        implements ContestSubmissionDao {

    @Inject
    public ContestSubmissionHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public ContestSubmissionModel createSubmissionModel() {
        return new ContestSubmissionModel();
    }
}
