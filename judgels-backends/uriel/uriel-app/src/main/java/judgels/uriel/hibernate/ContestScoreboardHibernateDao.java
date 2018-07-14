package judgels.uriel.hibernate;

import com.google.common.collect.ImmutableMap;
import java.time.Clock;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.persistence.ContestScoreboardDao;
import judgels.uriel.persistence.ContestScoreboardModel;
import judgels.uriel.persistence.ContestScoreboardModel_;
import org.hibernate.SessionFactory;

@Singleton
public class ContestScoreboardHibernateDao extends HibernateDao<ContestScoreboardModel> implements
        ContestScoreboardDao {

    @Inject
    public ContestScoreboardHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<ContestScoreboardModel> selectByContestJidAndType(String contestJid, ContestScoreboardType type) {
        return selectByUniqueColumns(ImmutableMap.of(
                ContestScoreboardModel_.contestJid, contestJid,
                ContestScoreboardModel_.type, type.name()));
    }
}
