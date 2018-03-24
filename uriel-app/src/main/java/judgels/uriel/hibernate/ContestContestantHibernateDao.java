package judgels.uriel.hibernate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.HibernateDao;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestContestantModel_;
import org.hibernate.SessionFactory;

@Singleton
public class ContestContestantHibernateDao extends HibernateDao<ContestContestantModel> implements
        ContestContestantDao {

    @Inject
    public ContestContestantHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Set<ContestContestantModel> selectAllByUserJids(String contestJid, List<String> userJids) {
        return ImmutableSet.copyOf(selectAllByColumnIn(
                ImmutableMap.of(ContestContestantModel_.contestJid, contestJid),
                ContestContestantModel_.userJid,
                userJids));
    }

    @Override
    public Page<ContestContestantModel> selectAllByContestJid(String contestJid, int page, int pageSize) {
        return selectAllByColumn(ContestContestantModel_.contestJid, contestJid, page, pageSize);
    }

    @Override
    public boolean existsByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByUniqueColumns(ImmutableMap.of(
                ContestContestantModel_.contestJid, contestJid,
                ContestContestantModel_.userJid, userJid)).isPresent();
    }
}
