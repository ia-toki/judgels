package judgels.uriel.hibernate;

import java.time.Clock;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.uriel.persistence.ContestStyleDao;
import judgels.uriel.persistence.ContestStyleModel;
import judgels.uriel.persistence.ContestStyleModel_;
import org.hibernate.SessionFactory;

@Singleton
public class ContestStyleHibernateDao extends HibernateDao<ContestStyleModel> implements ContestStyleDao {
    @Inject
    public ContestStyleHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<ContestStyleModel> selectByContestJid(String contestJid) {
        return selectByFilter(new FilterOptions.Builder<ContestStyleModel>()
                .putColumnsEq(ContestStyleModel_.contestJid, contestJid)
                .build());
    }
}
