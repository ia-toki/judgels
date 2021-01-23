package judgels.persistence.hibernate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.AbstractDAO;
import java.time.Clock;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.Model_;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.UnmodifiableModel;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.api.dump.DumpImportMode;
import judgels.persistence.api.dump.UnmodifiableDump;
import org.hibernate.query.Query;

public abstract class UnmodifiableHibernateDao<M extends UnmodifiableModel> extends AbstractDAO<M>
        implements UnmodifiableDao<M> {

    private final Clock clock;
    private final ActorProvider actorProvider;

    public UnmodifiableHibernateDao(HibernateDaoData data) {
        super(data.getSessionFactory());
        this.clock = data.getClock();
        this.actorProvider = data.getActorProvider();
    }

    @Override
    public void flush() {
        currentSession().flush();
    }

    @Override
    public void clear() {
        currentSession().clear();
    }

    @Override
    public M insert(M model) {
        model.createdBy = actorProvider.getJid().orElse(null);
        model.createdAt = clock.instant();
        model.createdIp = actorProvider.getIpAddress().orElse(null);

        return super.persist(model);
    }

    @Override
    public M persist(M model) {
        return super.persist(model);
    }

    @Override
    public List<M> insertAll(List<M> models) {
        return Lists.transform(models, this::insert);
    }

    @Override
    public M find(long id) {
        return select(id).orElse(null);
    }

    @Override
    public Optional<M> select(long id) {
        return Optional.ofNullable(get(id));
    }

    @Override
    public Optional<M> selectByFilter(FilterOptions<M> filterOptions) {
        // MySQL doesn't support empty IN() clause
        for (Collection<?> collection : filterOptions.getColumnsIn().values()) {
            if (collection.isEmpty()) {
                return Optional.empty();
            }
        }

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> cq = criteriaQuery();
        Root<M> root = cq.from(getEntityClass());

        applyFilters(cb, cq, root, filterOptions);

        return currentSession().createQuery(cq).uniqueResultOptional();
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
    public long selectCount(FilterOptions<M> filterOptions) {
        // MySQL doesn't support empty IN() clause
        for (Collection<?> collection : filterOptions.getColumnsIn().values()) {
            if (collection.isEmpty()) {
                return 0;
            }
        }

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<M> root = cq.from(getEntityClass());

        applyFilters(cb, cq, root, filterOptions);
        cq.select(cb.count(root));

        return currentSession().createQuery(cq).getSingleResult();
    }

    @Override
    public Page<M> selectPaged(FilterOptions<M> filterOptions, SelectionOptions selectionOptions) {
        List<M> page = selectAll(filterOptions, selectionOptions);
        long totalCount = selectCount(filterOptions);

        return new Page.Builder<M>()
                .totalCount(totalCount)
                .page(page)
                .build();
    }
    @Override
    public List<M> selectAll(FilterOptions<M> filterOptions, SelectionOptions selectionOptions) {
        // MySQL doesn't support empty IN() clause
        for (Collection<?> collection : filterOptions.getColumnsIn().values()) {
            if (collection.isEmpty()) {
                return Collections.emptyList();
            }
        }

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> cq = criteriaQuery();
        Root<M> root = cq.from(getEntityClass());

        applyFilters(cb, cq, root, filterOptions);

        List<Order> orders = Lists.newArrayList();

        if (selectionOptions.getOrderDir() == OrderDir.ASC) {
            orders.add(cb.asc(root.get(selectionOptions.getOrderBy())));
        } else {
            orders.add(cb.desc(root.get(selectionOptions.getOrderBy())));
        }
        if (selectionOptions.getOrderDir2().orElse(selectionOptions.getOrderDir()) == OrderDir.ASC) {
            orders.add(cb.asc(root.get(selectionOptions.getOrderBy2())));
        } else {
            orders.add(cb.desc(root.get(selectionOptions.getOrderBy2())));
        }
        cq.orderBy(orders);

        Query<M> query = currentSession().createQuery(cq);

        if (selectionOptions.getPageSize() > 0) {
            query.setFirstResult((selectionOptions.getPage() - 1) * selectionOptions.getPageSize());
            query.setMaxResults(selectionOptions.getPageSize());
        }

        return query.list();
    }

    @Override
    public void delete(M model) {
        currentSession().delete(model);
    }

    @Override
    public void setModelMetadataFromDump(M model, UnmodifiableDump dump) {
        if (dump.getMode() == DumpImportMode.RESTORE) {
            model.createdBy = dump.getCreatedBy().orElse(null);
            model.createdIp = dump.getCreatedIp().orElse(null);
            model.createdAt = dump.getCreatedAt().orElseThrow(
                    () -> new IllegalArgumentException("createdAt must be set if using RESTORE mode")
            );
        } else if (dump.getMode() == DumpImportMode.CREATE) {
            model.createdBy = actorProvider.getJid().orElse(null);
            model.createdIp = actorProvider.getIpAddress().orElse(null);
            model.createdAt = clock.instant();
        } else {
            throw new IllegalArgumentException(
                    String.format("Unknown mode: %s", dump.getMode())
            );
        }
    }

    @Override
    public List<M> getAll() {
        return selectAll(SelectionOptions.DEFAULT_ALL);
    }

    @Override
    public long countByFilters(String filterString) {
        return countByFilters(filterString, ImmutableMap.of(), ImmutableMap.of());
    }

    @Override
    public long countByFilters(
            String filterString,
            Map<SingularAttribute<? super M, ?>, ?> filterColumnsEq,
            Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn) {

        Map<SingularAttribute<? super M, String>, String> filterColumnsLike = filterString.equals("")
                ? ImmutableMap.of()
                : getColumnsFilterableByString()
                .stream()
                .collect(Collectors.toMap(e -> e, $ -> filterString));

        return selectCount(new FilterOptions.Builder<M>()
                .columnsEq(filterColumnsEq)
                .columnsIn(filterColumnsIn)
                .columnsLike(filterColumnsLike)
                .build());
    }

    @Override
    public long countByFiltersEq(String filterString, Map<SingularAttribute<? super M, ?>, ?> filterColumnsEq) {
        return countByFilters(filterString, filterColumnsEq, ImmutableMap.of());
    }

    @Override
    public long countByFiltersIn(
            String filterString,
            Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn) {

        return countByFilters(filterString, ImmutableMap.of(), filterColumnsIn);
    }

    @Override
    public List<M> findSortedByFilters(String orderBy, String orderDir, String filterStrin, long offset, long limit) {
        return findSortedByFilters(orderBy, orderDir, filterStrin, ImmutableMap.of(), ImmutableMap.of(), offset, limit);
    }

    @Override
    public List<M> findSortedByFilters(
            String orderBy,
            String orderDir,
            String filterString,
            Map<SingularAttribute<? super M, ?>, ?> filterColumnsEq,
            Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn,
            long offset,
            long limit) {

        Map<SingularAttribute<? super M, String>, String> filterColumnsLike = filterString.equals("")
                ? ImmutableMap.of()
                : getColumnsFilterableByString()
                .stream()
                .collect(Collectors.toMap(e -> e, $ -> filterString));

        SelectionOptions.Builder options = new SelectionOptions.Builder();
        if (limit != -1) {
            options.pageSize((int) limit);
            options.page((int) (offset / limit) + 1);
        }
        options.orderBy(orderBy);
        if (orderDir.equals("asc")) {
            options.orderDir(OrderDir.ASC);
        } else {
            options.orderDir(OrderDir.DESC);
        }

        return selectAll(new FilterOptions.Builder<M>()
                .columnsEq(filterColumnsEq)
                .columnsIn(filterColumnsIn)
                .columnsLike(filterColumnsLike)
                .build(), options.build());
    }

    @Override
    public List<M> findSortedByFiltersEq(
            String orderBy,
            String orderDir,
            String filterString,
            Map<SingularAttribute<? super M, ?>, ?> filterColumnsEq, long offset, long limit) {

        return findSortedByFilters(orderBy, orderDir, filterString, filterColumnsEq, ImmutableMap.of(), offset, limit);
    }

    @Override
    public List<M> findSortedByFiltersIn(
            String orderBy,
            String orderDir,
            String filterString,
            Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn,
            long offset,
            long limit) {

        return findSortedByFilters(orderBy, orderDir, filterString, ImmutableMap.of(), filterColumnsIn, offset, limit);
    }

    @Deprecated
    protected List<SingularAttribute<M, String>> getColumnsFilterableByString() {
        return ImmutableList.of();
    }

    private void applyFilters(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<M> root, FilterOptions<M> options) {
        Predicate filterId = cb.gt(root.get(Model_.id), options.getLastId());
        Predicate filterEq = cb.and(options.getColumnsEq().entrySet()
                .stream()
                .map(e -> cb.equal(root.get(e.getKey()), e.getValue()))
                .toArray(Predicate[]::new));
        Predicate filterIn = cb.and(options.getColumnsIn().entrySet()
                .stream()
                .map(e -> root.get(e.getKey()).in(e.getValue()))
                .toArray(Predicate[]::new));
        Predicate filterLike = options.getColumnsLike().isEmpty() ? cb.and() : cb.or(options.getColumnsLike().entrySet()
                .stream()
                .map(e -> cb.like(root.get(e.getKey()), contains(e.getValue())))
                .toArray(Predicate[]::new));
        Predicate filterCustom = cb.and(options.getCustomPredicates()
                .stream()
                .map(f -> f.apply(cb, cq, root))
                .toArray(Predicate[]::new));

        cq.where(filterId, filterEq, filterIn, filterLike, filterCustom);
    }

    private static String contains(String str) {
        return "%" + str + "%";
    }
}
