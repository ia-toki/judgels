package judgels.persistence.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import judgels.persistence.CriteriaPredicate;
import judgels.persistence.QueryBuilder;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class HibernateQueryBuilder<M> implements QueryBuilder<M> {
    private final Session currentSession;
    private final Class<M> entityClass;

    private final List<CriteriaPredicate<M>> predicates;
    private final List<String> orderColumns;
    private final List<OrderDir> orderDirs;
    private int pageNumber;
    private int pageSize;

    public HibernateQueryBuilder(Session currentSession, Class<M> entityClass) {
        this.currentSession = currentSession;
        this.entityClass = entityClass;

        this.predicates = new ArrayList<>();
        this.orderColumns = new ArrayList<>();
        this.orderDirs = new ArrayList<>();
        this.pageNumber = 0;
        this.pageSize = 0;
    }

    @Override
    public QueryBuilder<M> where(CriteriaPredicate<M> predicate) {
        predicates.add(predicate);
        return this;
    }

    @Override
    public QueryBuilder<M> orderBy(String column, OrderDir dir) {
        orderColumns.add(column);
        orderDirs.add(dir);
        return this;
    }

    @Override
    public QueryBuilder<M> pageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    @Override
    public QueryBuilder<M> pageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    @Override
    public int count() {
        CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<M> root = cq.from(entityClass);

        applyPredicates(cb, cq, root);
        cq.select(cb.count(root));

        return (int) (long) currentSession.createQuery(cq).getSingleResult();
    }

    @Override
    public Optional<M> unique() {
        CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        CriteriaQuery<M> cq = cb.createQuery(entityClass);
        Root<M> root = cq.from(entityClass);

        applyPredicates(cb, cq, root);
        return currentSession.createQuery(cq).uniqueResultOptional();
    }

    @Override
    public List<M> all() {
        CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        CriteriaQuery<M> cq = cb.createQuery(entityClass);
        Root<M> root = cq.from(entityClass);

        applyPredicates(cb, cq, root);
        applyOrders(cb, cq, root);

        Query<M> query = currentSession.createQuery(cq);
        if (pageSize > 0) {
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);
        }
        return query.list();
    }

    @Override
    public Page<M> paged() {
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        if (pageSize == 0) {
            pageSize = 20;
        }

        List<M> page = all();
        int totalCount = count();

        return new Page.Builder<M>()
                .totalCount(totalCount)
                .page(page)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();
    }

    private void applyPredicates(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<M> root) {
        cq.where(predicates
                .stream()
                .map(p -> p.apply(cb, cq, root))
                .toArray(Predicate[]::new));
    }

    private void applyOrders(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<M> root) {
        if (orderColumns.isEmpty()) {
            orderColumns.add(UnmodifiableModel_.ID);
            orderDirs.add(OrderDir.DESC);
        }

        List<Order> orders = new ArrayList<>();

        for (int i = 0; i < orderColumns.size(); i++) {
            if (orderDirs.get(i) == OrderDir.ASC) {
                orders.add(cb.asc(root.get(orderColumns.get(i))));
            } else {
                orders.add(cb.desc(root.get(orderColumns.get(i))));
            }
        }
        cq.orderBy(orders);
    }
}
