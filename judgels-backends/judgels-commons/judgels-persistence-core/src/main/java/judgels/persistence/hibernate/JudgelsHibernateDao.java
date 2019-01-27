package judgels.persistence.hibernate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.ActorProvider;
import judgels.persistence.JidGenerator;
import judgels.persistence.JudgelsDao;
import judgels.persistence.JudgelsModel;
import judgels.persistence.JudgelsModel_;
import org.hibernate.SessionFactory;

public abstract class JudgelsHibernateDao<M extends JudgelsModel> extends HibernateDao<M> implements JudgelsDao<M> {
    private final Clock clock;

    public JudgelsHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);

        this.clock = clock;
    }

    @Override
    public M insert(M model) {
        model.jid = JidGenerator.newJid(getEntityClass());
        return super.insert(model);
    }

    @Override
    public Map<String, M> selectByJids(Set<String> jids) {
        if (jids.isEmpty()) {
            return ImmutableMap.of();
        }

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> cq = cb.createQuery(getEntityClass());
        Root<M> root = cq.from(getEntityClass());

        cq.where(root.get(JudgelsModel_.jid).in(jids));

        List<M> result = currentSession().createQuery(cq).getResultList();
        return result.stream().collect(Collectors.toMap(p -> p.jid, p -> p));
    }

    @Override
    public Optional<M> selectByJid(String jid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> cq = cb.createQuery(getEntityClass());
        Root<M> root = cq.from(getEntityClass());
        cq.where(cb.equal(root.get(JudgelsModel_.jid), jid));
        return currentSession().createQuery(cq).uniqueResultOptional();
    }

    @Override
    public M updateByJid(String jid, M model) {
        model.jid = jid;
        return super.update(model);
    }

    @Override
    public void persist(M model, String actor, String ipAddress) {
        model.jid = JidGenerator.newJid(getEntityClass());
        model.createdBy = actor;
        model.createdAt = clock.instant();
        model.createdIp = ipAddress;

        model.updatedBy = model.createdBy;
        model.updatedAt = model.createdAt;
        model.updatedIp = model.createdIp;

        persist(model);
    }

    @Override
    public void persist(M model, int childIndex, String actor, String ipAddress) {
        model.jid = JidGenerator.newChildJid(getEntityClass(), childIndex);
        model.createdBy = actor;
        model.createdAt = clock.instant();
        model.createdIp = ipAddress;

        model.updatedBy = model.createdBy;
        model.updatedAt = model.createdAt;
        model.updatedIp = model.createdIp;

        persist(model);
    }

    @Override
    public boolean existsByJid(String jid) {
        return selectByJid(jid).isPresent();
    }

    @Override
    public M findByJid(String jid) {
        return selectByJid(jid).orElse(null);
    }

    @Override
    public List<M> getByJids(Collection<String> jids) {
        Map<String, M> map = selectByJids(ImmutableSet.copyOf(jids));
        return jids.stream().map(map::get).collect(Collectors.toList());
    }
}
