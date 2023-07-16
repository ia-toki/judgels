package judgels.persistence.hibernate;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.JidGenerator;
import judgels.persistence.JudgelsDao;
import judgels.persistence.JudgelsModel;
import judgels.persistence.JudgelsModel_;

public abstract class JudgelsHibernateDao<M extends JudgelsModel> extends HibernateDao<M> implements JudgelsDao<M> {
    public JudgelsHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public M insert(M model) {
        model.jid = JidGenerator.newJid(getEntityClass());
        return super.insert(model);
    }

    @Override
    public M insertWithJid(String jid, M model) {
        model.jid = jid;
        return super.insert(model);
    }

    @Override
    public Map<String, M> selectByJids(Collection<String> jids) {
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
    public boolean existsByJid(String jid) {
        return selectByJid(jid).isPresent();
    }

    @Override
    public M findByJid(String jid) {
        return selectByJid(jid).orElse(null);
    }
}
