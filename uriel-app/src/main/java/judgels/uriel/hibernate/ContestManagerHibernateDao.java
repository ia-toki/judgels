package judgels.uriel.hibernate;

import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.ActorProvider;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.HibernateDao;
import judgels.uriel.contest.manager.ContestManagerDao;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestManagerModel_;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class ContestManagerHibernateDao extends HibernateDao<ContestManagerModel> implements ContestManagerDao {
    @Inject
    public ContestManagerHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Set<ContestManagerModel> selectAllByUserJids(String contestJid, Set<String> userJids) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ContestManagerModel> cq = cb.createQuery(ContestManagerModel.class);
        Root<ContestManagerModel> root = cq.from(ContestManagerModel.class);

        cb.and(cb.equal(root.get(ContestManagerModel_.contestJid), contestJid),
                root.get(ContestManagerModel_.userJid).in(userJids));

        return ImmutableSet.copyOf(currentSession().createQuery(cq).getResultList());
    }

    @Override
    public Set<ContestManagerModel> insertAll(String contestJid, Set<ContestManagerModel> managers) {
        ImmutableSet.Builder<ContestManagerModel> persistedModels = ImmutableSet.builder();
        for (ContestManagerModel manager : managers) {
            ContestManagerModel persistedModel = insert(manager);
            persistedModels.add(persistedModel);
        }

        return persistedModels.build();
    }

    @Override
    public Page<ContestManagerModel> selectAllByContestJid(String contestJid, int page, int pageSize) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ContestManagerModel> cq = cb.createQuery(ContestManagerModel.class);
        Root<ContestManagerModel> root = cq.from(getEntityClass());

        cq.where(cb.equal(root.get(ContestManagerModel_.contestJid), contestJid));

        Query<ContestManagerModel> query = currentSession().createQuery(cq);

        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);

        List<ContestManagerModel> data = query.list();
        long totalData = selectCountByContestJid(contestJid);

        return new Page.Builder<ContestManagerModel>()
                .totalData(totalData)
                .data(data)
                .build();
    }

    @Override
    public boolean existsByContestJidAndUserJid(String contestJid, String userJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ContestManagerModel> cq = cb.createQuery(getEntityClass());
        Root<ContestManagerModel> root = cq.from(getEntityClass());
        cq.where(cb.and(
                cb.equal(root.get(ContestManagerModel_.contestJid), contestJid),
                cb.equal(root.get(ContestManagerModel_.userJid), userJid)));

        Optional<ContestManagerModel> model = currentSession().createQuery(cq).uniqueResultOptional();
        return model.isPresent();
    }

    private long selectCountByContestJid(String contestJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ContestManagerModel> root = cq.from(getEntityClass());

        cq.where(cb.equal(root.get(ContestManagerModel_.contestJid), contestJid));
        cq.select(cb.count(root));

        Query<Long> query = currentSession().createQuery(cq);
        return query.getSingleResult();
    }
}
