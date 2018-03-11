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
import judgels.uriel.contest.supervisor.ContestSupervisorDao;
import judgels.uriel.contest.supervisor.ContestSupervisorModel;
import judgels.uriel.contest.supervisor.ContestSupervisorModel_;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class ContestSupervisorHibernateDao extends HibernateDao<ContestSupervisorModel>
        implements ContestSupervisorDao {

    @Inject
    public ContestSupervisorHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Set<ContestSupervisorModel> selectAllByUserJids(String contestJid, Set<String> userJids) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ContestSupervisorModel> cq = cb.createQuery(ContestSupervisorModel.class);
        Root<ContestSupervisorModel> root = cq.from(ContestSupervisorModel.class);

        cb.and(cb.equal(root.get(ContestSupervisorModel_.contestJid), contestJid),
                root.get(ContestSupervisorModel_.userJid).in(userJids));

        return ImmutableSet.copyOf(currentSession().createQuery(cq).getResultList());
    }

    @Override
    public Set<ContestSupervisorModel> insertAll(String contestJid, Set<ContestSupervisorModel> supervisors) {
        ImmutableSet.Builder<ContestSupervisorModel> persistedModels = ImmutableSet.builder();
        for (ContestSupervisorModel supervisor : supervisors) {
            ContestSupervisorModel persistedModel = insert(supervisor);
            persistedModels.add(persistedModel);
        }

        return persistedModels.build();
    }

    @Override
    public Page<ContestSupervisorModel> selectAllByContestJid(String contestJid, int page, int pageSize) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ContestSupervisorModel> cq = cb.createQuery(ContestSupervisorModel.class);
        Root<ContestSupervisorModel> root = cq.from(getEntityClass());

        cq.where(cb.equal(root.get(ContestSupervisorModel_.contestJid), contestJid));

        Query<ContestSupervisorModel> query = currentSession().createQuery(cq);

        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);

        List<ContestSupervisorModel> data = query.list();
        long totalData = selectCountByContestJid(contestJid);

        return new Page.Builder<ContestSupervisorModel>()
                .totalData(totalData)
                .data(data)
                .build();
    }

    @Override
    public boolean existsByContestJidAndUserJid(String contestJid, String userJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ContestSupervisorModel> cq = cb.createQuery(getEntityClass());
        Root<ContestSupervisorModel> root = cq.from(getEntityClass());
        cq.where(cb.and(
                cb.equal(root.get(ContestSupervisorModel_.contestJid), contestJid),
                cb.equal(root.get(ContestSupervisorModel_.userJid), userJid)));

        Optional<ContestSupervisorModel> model = currentSession().createQuery(cq).uniqueResultOptional();
        return model.isPresent();
    }

    private long selectCountByContestJid(String contestJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ContestSupervisorModel> root = cq.from(getEntityClass());

        cq.where(cb.equal(root.get(ContestSupervisorModel_.contestJid), contestJid));
        cq.select(cb.count(root));

        Query<Long> query = currentSession().createQuery(cq);
        return query.getSingleResult();
    }
}
