package judgels.uriel.hibernate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
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
    public Set<ContestContestantModel> selectAllByContestJidAndUserJids(String contestJid, List<String> userJids) {
        return ImmutableSet.copyOf(selectAll(new FilterOptions.Builder<ContestContestantModel>()
                .putColumnsEq(ContestContestantModel_.contestJid, contestJid)
                .putColumnsIn(ContestContestantModel_.userJid, userJids)
                .build()).getData());
    }

    @Override
    public Page<ContestContestantModel> selectAllByContestJid(String contestJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ContestContestantModel>()
                .putColumnsEq(ContestContestantModel_.contestJid, contestJid)
                .build(), options);
    }

    @Override
    public boolean existsByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByUniqueColumns(ImmutableMap.of(
                ContestContestantModel_.contestJid, contestJid,
                ContestContestantModel_.userJid, userJid)).isPresent();
    }
}
