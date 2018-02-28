package judgels.uriel.hibernate;

import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.util.List;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.ActorProvider;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.HibernateDao;
import judgels.uriel.contest.contestant.ContestContestantDao;
import judgels.uriel.contest.contestant.ContestContestantModel;
import judgels.uriel.contest.contestant.ContestContestantModel_;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class ContestContestantHibernateDao extends HibernateDao<ContestContestantModel> implements
        ContestContestantDao {
    public ContestContestantHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Set<ContestContestantModel> selectAllByUserJids(String contestJid, Set<String> userJids) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ContestContestantModel> cq = cb.createQuery(ContestContestantModel.class);
        Root<ContestContestantModel> root = cq.from(ContestContestantModel.class);

        cb.and(cb.equal(root.get(ContestContestantModel_.contestJid), contestJid),
                root.get(ContestContestantModel_.userJid).in(userJids));

        return ImmutableSet.copyOf(currentSession().createQuery(cq).getResultList());
    }

    @Override
    public Set<ContestContestantModel> insertAll(String contestJid, Set<ContestContestantModel> contestants) {
        ImmutableSet.Builder<ContestContestantModel> persistedModels = ImmutableSet.builder();
        for (ContestContestantModel contestant : contestants) {
            ContestContestantModel persistedModel = insert(contestant);
            persistedModels.add(persistedModel);
        }

        return persistedModels.build();
    }

    @Override
    public Page<ContestContestantModel> selectAllByContestJid(String contestJid, int page, int pageSize) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ContestContestantModel> cq = cb.createQuery(ContestContestantModel.class);
        Root<ContestContestantModel> root = cq.from(getEntityClass());

        cq.where(cb.equal(root.get(ContestContestantModel_.contestJid), contestJid));

        Query<ContestContestantModel> query = currentSession().createQuery(cq);

        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);

        List<ContestContestantModel> data = query.list();
        long totalData = selectCount();
        long totalPages = (totalData + pageSize - 1) / pageSize;

        return new Page.Builder<ContestContestantModel>()
                .currentPage(page)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .totalData(totalData)
                .data(data)
                .build();
    }

}
