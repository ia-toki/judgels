package judgels.uriel.hibernate;

import com.google.common.collect.ImmutableMap;
import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import judgels.uriel.persistence.ContestSupervisorDao;
import judgels.uriel.persistence.ContestSupervisorModel;
import judgels.uriel.persistence.ContestSupervisorModel_;
import org.hibernate.SessionFactory;

@Singleton
public class ContestSupervisorHibernateDao extends HibernateDao<ContestSupervisorModel> implements
        ContestSupervisorDao {

    @Inject
    public ContestSupervisorHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public boolean existsByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByUniqueColumns(ImmutableMap.of(
                ContestSupervisorModel_.contestJid, contestJid,
                ContestSupervisorModel_.userJid, userJid)).isPresent();
    }
}
