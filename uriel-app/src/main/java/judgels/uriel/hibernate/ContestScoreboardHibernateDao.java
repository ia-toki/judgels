package judgels.uriel.hibernate;

import java.time.Clock;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.contest.scoreboard.ContestScoreboardDao;
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
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ContestScoreboardModel> cq = cb.createQuery(ContestScoreboardModel.class);
        Root<ContestScoreboardModel> root = cq.from(ContestScoreboardModel.class);
        cq.where(
                cb.and(
                        cb.equal(root.get(ContestScoreboardModel_.contestJid), contestJid),
                        cb.equal(root.get(ContestScoreboardModel_.type), type.name())));
        return currentSession().createQuery(cq).uniqueResultOptional();
    }
}
