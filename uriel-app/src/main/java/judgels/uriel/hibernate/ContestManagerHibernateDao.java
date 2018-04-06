package judgels.uriel.hibernate;

import com.google.common.collect.ImmutableMap;
import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestManagerModel_;
import org.hibernate.SessionFactory;

@Singleton
public class ContestManagerHibernateDao extends HibernateDao<ContestManagerModel> implements ContestManagerDao {
    @Inject
    public ContestManagerHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public boolean existsByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByUniqueColumns(ImmutableMap.of(
                ContestManagerModel_.contestJid, contestJid,
                ContestManagerModel_.userJid, userJid)).isPresent();
    }
}
