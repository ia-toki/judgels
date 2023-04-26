package judgels.persistence.hibernate;

import java.util.ArrayList;
import java.util.List;
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
    private String orderBy;
    private OrderDir orderDir;
    private String orderBy2;
    private OrderDir orderDir2;
    private int pageNumber;
    private int pageSize;

    public HibernateQueryBuilder(Session currentSession, Class<M> entityClass) {
        this.currentSession = currentSession;
        this.entityClass = entityClass;

        this.predicates = new ArrayList<>();
        this.orderBy = UnmodifiableModel_.ID;
        this.orderDir = OrderDir.DESC;
        this.pageNumber = 0;
        this.pageSize = 0;
    }

    @Override
    public QueryBuilder<M> where(CriteriaPredicate<M> predicate) {
        predicates.add(predicate);
        return this;
    }

    @Override
    public QueryBuilder<M> orderBy(String column) {
        this.orderBy = column;
        return this;
    }

    @Override
    public QueryBuilder<M> orderDir(OrderDir dir) {
        this.orderDir = dir;
        return this;
    }

    @Override
    public QueryBuilder<M> orderBy2(String column) {
        this.orderBy2 = column;
        return this;
    }

    @Override
    public QueryBuilder<M> orderDir2(OrderDir dir) {
        this.orderDir2 = dir;
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
    public List<M> list() {
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
    public Page<M> listPaged() {
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        if (pageSize == 0) {
            pageSize = 20;
        }

        List<M> page = list();
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
        List<Order> orders = new ArrayList<>();
        if (orderDir == OrderDir.ASC) {
            orders.add(cb.asc(root.get(orderBy)));
        } else {
            orders.add(cb.desc(root.get(orderBy)));
        }
        if (orderBy2 != null) {
            if (orderDir2 == OrderDir.ASC) {
                orders.add(cb.asc(root.get(orderBy2)));
            } else {
                orders.add(cb.desc(root.get(orderBy2)));
            }
        }
        cq.orderBy(orders);
    }
}
