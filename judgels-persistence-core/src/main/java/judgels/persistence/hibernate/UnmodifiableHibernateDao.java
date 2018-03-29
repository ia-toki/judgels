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
import judgels.persistence.OrderDir;
import judgels.persistence.SelectAllOptions;
import judgels.persistence.SelectCountOptions;
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
    public long selectCount(SelectCountOptions<M> options) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<M> root = cq.from(getEntityClass());

        Predicate filterEq = cb.and(options.getFilterColumnsEq().entrySet()
                .stream()
                .map(e -> cb.equal(root.get(e.getKey()), e.getValue()))
                .toArray(Predicate[]::new));
        Predicate filterIn = cb.and(options.getFilterColumnsIn().entrySet()
                .stream()
                .map(e -> root.get(e.getKey()).in(e.getValue()))
                .toArray(Predicate[]::new));

        cq.select(cb.count(root)).where(cb.and(filterEq, filterIn));
        return currentSession().createQuery(cq).getSingleResult();
    }

    @Override
    public Page<M> selectAll(SelectAllOptions<M> options) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> cq = criteriaQuery();
        Root<M> root = cq.from(getEntityClass());

        Predicate filterEq = cb.and(options.getFilterColumnsEq().entrySet()
                .stream()
                .map(e -> cb.equal(root.get(e.getKey()), e.getValue()))
                .toArray(Predicate[]::new));
        Predicate filterIn = cb.and(options.getFilterColumnsIn().entrySet()
                .stream()
                .map(e -> root.get(e.getKey()).in(e.getValue()))
                .toArray(Predicate[]::new));

        cq.where(cb.and(filterEq, filterIn));

        if (options.getOrderDir() == OrderDir.ASC) {
            cq.orderBy(cb.asc(root.get(options.getOrderBy())));
        } else {
            cq.orderBy(cb.desc(root.get(options.getOrderBy())));
        }

        Query<M> query = currentSession().createQuery(cq);

        if (options.getPageSize() > 0) {
            query.setFirstResult((options.getPage() - 1) * options.getPageSize());
            query.setMaxResults(options.getPageSize());
        }

        List<M> data = query.list();
        long totalData = selectCount(new SelectCountOptions.Builder<M>()
                .filterColumnsEq(options.getFilterColumnsEq())
                .filterColumnsIn(options.getFilterColumnsIn())
                .build());

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
