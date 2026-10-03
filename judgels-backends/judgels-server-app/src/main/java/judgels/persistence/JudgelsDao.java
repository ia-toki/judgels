package judgels.persistence;

import com.google.common.collect.ImmutableMap;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class JudgelsDao<M extends JudgelsModel> extends Dao<M> {
    public JudgelsDao(DaoData data) {
        super(data);
    }

    @Override
    public M insert(M model) {
        model.jid = JidGenerator.newJid(getEntityClass());
        return super.insert(model);
    }

    public M insertWithJid(String jid, M model) {
        model.jid = jid;
        return super.insert(model);
    }

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

    public Optional<M> selectByJid(String jid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> cq = cb.createQuery(getEntityClass());
        Root<M> root = cq.from(getEntityClass());
        cq.where(cb.equal(root.get(JudgelsModel_.jid), jid));
        return currentSession().createQuery(cq).uniqueResultOptional();
    }

    public M updateByJid(String jid, M model) {
        model.jid = jid;
        return super.update(model);
    }

    public boolean existsByJid(String jid) {
        return selectByJid(jid).isPresent();
    }

    public M findByJid(String jid) {
        return selectByJid(jid).orElse(null);
    }
}
