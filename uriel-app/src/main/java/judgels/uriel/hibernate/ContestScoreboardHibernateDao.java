package judgels.uriel.hibernate;

import java.time.Clock;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import judgels.uriel.contest.ContestScoreboardDao;
import judgels.uriel.contest.ContestScoreboardModel;
import judgels.uriel.contest.ContestScoreboardModel_;
import org.hibernate.SessionFactory;

public class ContestScoreboardHibernateDao extends HibernateDao<ContestScoreboardModel> implements
        ContestScoreboardDao {
    public ContestScoreboardHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<ContestScoreboardModel> selectByContestJid(String contestJid, boolean isOfficial) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ContestScoreboardModel> cq = cb.createQuery(ContestScoreboardModel.class);
        Root<ContestScoreboardModel> root = cq.from(ContestScoreboardModel.class);
        cq.where(cb.and(cb.equal(root.get(ContestScoreboardModel_.contestJid), contestJid),
                        cb.equal(root.get(ContestScoreboardModel_.isOfficial), isOfficial)));
        return currentSession().createQuery(cq).uniqueResultOptional();
    }
}
