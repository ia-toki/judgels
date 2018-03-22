package judgels.persistence.hibernate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.AbstractDAO;
import java.sql.Date;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import judgels.persistence.ActorProvider;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.UnmodifiableModel;
import judgels.persistence.api.Page;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public abstract class UnmodifiableHibernateDao<M extends UnmodifiableModel> extends AbstractDAO<M>
        implements UnmodifiableDao<M> {

    private final Clock clock;
    private final ActorProvider actorProvider;

    public UnmodifiableHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory);
        this.clock = clock;
        this.actorProvider = actorProvider;
    }

    @Override
    public M insert(M model) {
        model.createdBy = actorProvider.getJid().orElse(null);
        model.createdAt = Date.from(clock.instant());
        model.createdIp = actorProvider.getIpAddress().orElse(null);

        return persist(model);
    }

    @Override
    public List<M> insertAll(List<M> models) {
        return Lists.transform(models, this::insert);
    }

    @Override
    public Optional<M> select(long id) {
        return Optional.ofNullable(get(id));
    }

    @Override
    public Optional<M> selectByUniqueColumn(SingularAttribute<M, String> column, String value) {
        return selectByUniqueColumns(ImmutableMap.of(column, value));
    }

    @Override
    public Optional<M> selectByUniqueColumns(Map<SingularAttribute<M, ?>, ?> key) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> cq = criteriaQuery();
        Root<M> root = cq.from(getEntityClass());
        cq.where(cb.and(key.entrySet()
                .stream()
                .map(e -> cb.equal(root.get(e.getKey()), e.getValue()))
                .toArray(Predicate[]::new)));
        return currentSession().createQuery(cq).uniqueResultOptional();
    }

    @Override
    public long selectCount() {
        return selectCountByColumns(ImmutableMap.of());
    }

    @Override
    public long selectCountByColumn(SingularAttribute<M, String> column, String value) {
        return selectCountByColumns(ImmutableMap.of(column, value));
    }

    @Override
    public long selectCountByColumns(Map<SingularAttribute<M, ?>, ?> key) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<M> root = cq.from(getEntityClass());
        cq.select(cb.count(root)).where(cb.and(key.entrySet()
                .stream()
                .map(e -> cb.equal(root.get(e.getKey()), e.getValue()))
                .toArray(Predicate[]::new)));
        return currentSession().createQuery(cq).getSingleResult();
    }

    @Override
    public Page<M> selectAll(int page, int pageSize) {
        return selectAllByColumns(ImmutableMap.of(), page, pageSize);
    }

    @Override
    public Page<M> selectAllByColumn(SingularAttribute<M, String> column, String value, int page, int pageSize) {
        return selectAllByColumns(ImmutableMap.of(column, value), page, pageSize);
    }

    @Override
    public Page<M> selectAllByColumns(Map<SingularAttribute<M, ?>, ?> key, int page, int pageSize) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> cq = criteriaQuery();
        Root<M> root = cq.from(getEntityClass());
        cq.where(cb.and(key.entrySet()
                .stream()
                .map(e -> cb.equal(root.get(e.getKey()), e.getValue()))
                .toArray(Predicate[]::new)));

        Query<M> query = currentSession().createQuery(cq);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);

        List<M> data = query.list();
        long totalData = selectCountByColumns(key);

        return new Page.Builder<M>()
                .totalData(totalData)
                .data(data)
                .build();
    }

    @Override
    public void delete(M model) {
        currentSession().delete(model);
    }
}
