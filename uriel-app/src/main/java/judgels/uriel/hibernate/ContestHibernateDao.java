package judgels.uriel.hibernate;

import java.time.Clock;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.ActorProvider;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestContestantModel_;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

@Singleton
public class ContestHibernateDao extends JudgelsHibernateDao<ContestModel> implements ContestDao {
    @Inject
    public ContestHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Page<ContestModel> selectAllByUserJid(String userJid, SelectionOptions options) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ContestModel> cq = cb.createQuery(ContestModel.class);
        Root<ContestModel> root = cq.from(ContestModel.class);

        cq.where(cb.exists(isContestant(cq, root.get(ContestModel_.jid), userJid)));

        Query<ContestModel> query = currentSession().createQuery(cq);

        query.setFirstResult((options.getPage() - 1) * options.getPageSize());
        query.setMaxResults(options.getPageSize());

        List<ContestModel> data = query.list();
        long totalData = selectCountByUserJid(userJid);

        return new Page.Builder<ContestModel>()
                .totalData(totalData)
                .data(data)
                .build();
    }

    private long selectCountByUserJid(String userJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ContestModel> root = cq.from(ContestModel.class);

        cq.where(cb.exists(isContestant(cq, root.get(ContestModel_.jid), userJid)));
        cq.select(cb.count(root));

        return currentSession().createQuery(cq).getSingleResult();
    }

    private Subquery<ContestContestantModel> isContestant(CriteriaQuery<?> cq, Path<?> contestJid, String userJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        Subquery<ContestContestantModel> sq = cq.subquery(ContestContestantModel.class);
        Root<ContestContestantModel> root = sq.from(ContestContestantModel.class);

        sq.where(
                cb.and(
                        cb.equal(root.get(ContestContestantModel_.contestJid), contestJid),
                        cb.equal(root.get(ContestContestantModel_.userJid), userJid)
                )
        );
        sq.select(root);

        return sq;
    }
}
