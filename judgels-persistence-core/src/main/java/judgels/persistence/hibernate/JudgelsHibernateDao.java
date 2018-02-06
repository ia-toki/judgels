package judgels.persistence.hibernate;

import java.time.Clock;
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
    public JudgelsHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public M insert(M model) {
        model.jid = JidGenerator.newJid(getEntityClass());
        return super.insert(model);
    }

    @Override
    public Map<String, M> selectByJids(Set<String> jids) {
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
}
