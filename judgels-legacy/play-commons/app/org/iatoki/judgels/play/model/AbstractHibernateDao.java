package org.iatoki.judgels.play.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import play.db.jpa.JPA;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractHibernateDao<K, M extends AbstractModel> extends AbstractDao<K, M> {

    protected AbstractHibernateDao(Class<M> modelClass) {
        super(modelClass);
    }

    @Override
    public void persist(M model, String user, String ipAddress) {
        model.userCreate = user;
        model.timeCreate = System.currentTimeMillis();
        model.ipCreate = ipAddress;

        model.userUpdate = user;
        model.timeUpdate = model.timeCreate;
        model.ipUpdate = ipAddress;

        JPA.em().persist(model);
    }

    @Override
    public M edit(M model, String user, String ipAddress) {
        model.userUpdate = user;
        model.timeUpdate = System.currentTimeMillis();
        model.ipUpdate = ipAddress;

        return JPA.em().merge(model);
    }

    @Override
    public void flush() {
        JPA.em().flush();
    }

    @Override
    public void remove(M model) {
        JPA.em().remove(model);
    }

    @Override
    public boolean existsById(K id) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<M> root = query.from(getModelClass());

        query
                .select(cb.count(root))
                .where(cb.equal(root.get("id"), id));

        return JPA.em().createQuery(query).getSingleResult() > 0;
    }

    @Override
    public M findById(K id) {
        return JPA.em().find(getModelClass(), id);
    }

    @Override
    public List<M> getAll() {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());

        query.from(getModelClass());

        return JPA.em().createQuery(query).getResultList();
    }

    @Override
    public long countByFilters(String filterString) {
        return countByFilters(filterString, ImmutableMap.of(), ImmutableMap.of());
    }

    @Override
    public long countByFilters(String filterString, Map<SingularAttribute<? super M, ? extends Object>, ? extends Object> filterColumnsEq, Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn) {
        for (Collection<String> values : filterColumnsIn.values()) {
            if (values.isEmpty()) {
                return 0;
            }
        }

        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<M> root = query.from(getModelClass());

        Predicate byString;
        if (getColumnsFilterableByString().isEmpty()) {
            byString = cb.and();
        } else {
            List<Predicate> byStringPredicates = Lists.transform(getColumnsFilterableByString(), c -> cb.like(root.get(c), "%" + filterString + "%"));
            byString = cb.or(byStringPredicates.toArray(new Predicate[byStringPredicates.size()]));
        }

        List<Predicate> byColumnPredicates = filterColumnsEq.entrySet().stream().map(e -> cb.equal(root.get(e.getKey()), e.getValue())).collect(Collectors.toList());
        List<Predicate> byColumnInPredicates = filterColumnsIn.entrySet().stream().filter(e -> ((e != null) && (!e.getValue().isEmpty()))).map(e -> root.get(e.getKey()).in(e.getValue())).collect(Collectors.toList());
        Predicate byColumn = cb.and(byColumnPredicates.toArray(new Predicate[byColumnPredicates.size()]));
        Predicate byColumnIn = cb.and(byColumnInPredicates.toArray(new Predicate[byColumnInPredicates.size()]));

        query
                .select(cb.count(root))
                .where(cb.and(byString, byColumn, byColumnIn));

        return JPA.em().createQuery(query).getSingleResult();
    }

    @Override
    public long countByFiltersEq(String filterString, Map<SingularAttribute<? super M, ? extends Object>, ? extends Object> filterColumnsEq) {
        return countByFilters(filterString, filterColumnsEq, ImmutableMap.of());
    }

    @Override
    public long countByFiltersIn(String filterString, Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn) {
        return countByFilters(filterString, ImmutableMap.of(), filterColumnsIn);
    }

    @Override
    public List<M> findSortedByFilters(String orderBy, String orderDir, String filterString, long offset, long limit) {
        return findSortedByFilters(orderBy, orderDir, filterString, ImmutableMap.of(), ImmutableMap.of(), offset, limit);
    }

    @Override
    public List<M> findSortedByFilters(String orderBy, String orderDir, String filterString, Map<SingularAttribute<? super M, ? extends Object>, ? extends Object> filterColumnsEq, Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn, long offset, long limit) {
        for (Collection<String> values : filterColumnsIn.values()) {
            if (values.isEmpty()) {
                return ImmutableList.of();
            }
        }

        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());
        Root<M> root = query.from(getModelClass());

        Predicate byString;
        if (getColumnsFilterableByString().isEmpty()) {
            byString = cb.and();
        } else {
            List<Predicate> byStringPredicates = Lists.transform(getColumnsFilterableByString(), c -> cb.like(root.get(c), "%" + filterString + "%"));
            byString = cb.or(byStringPredicates.toArray(new Predicate[byStringPredicates.size()]));
        }

        List<Predicate> byColumnPredicates = filterColumnsEq.entrySet().stream().map(e -> cb.equal(root.get(e.getKey()), e.getValue())).collect(Collectors.toList());
        List<Predicate> byColumnInPredicates = filterColumnsIn.entrySet().stream().filter(e -> ((e != null) && (!e.getValue().isEmpty()))).map(e -> root.get(e.getKey()).in(e.getValue())).collect(Collectors.toList());
        Predicate byColumn = cb.and(byColumnPredicates.toArray(new Predicate[byColumnPredicates.size()]));
        Predicate byColumnIn = cb.and(byColumnInPredicates.toArray(new Predicate[byColumnInPredicates.size()]));

        Order order;
        if ("asc".equals(orderDir)) {
            order = cb.asc(root.get(orderBy));
        } else {
            order = cb.desc(root.get(orderBy));
        }

        query
                .where(cb.and(byString, byColumn, byColumnIn))
                .orderBy(order);

        TypedQuery<M> q = JPA.em().createQuery(query).setFirstResult((int) offset);
        if (limit != -1) {
            q.setMaxResults((int) limit);
        }

        return q.getResultList();
    }

    @Override
    public List<M> findSortedByFiltersEq(String orderBy, String orderDir, String filterString, Map<SingularAttribute<? super M, ? extends Object>, ? extends Object> filterColumnsEq, long offset, long limit) {
        return findSortedByFilters(orderBy, orderDir, filterString, filterColumnsEq, ImmutableMap.of(), offset, limit);
    }

    @Override
    public List<M> findSortedByFiltersIn(String orderBy, String orderDir, String filterString, Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn, long offset, long limit) {
        return findSortedByFilters(orderBy, orderDir, filterString, ImmutableMap.of(), filterColumnsIn, offset, limit);
    }

    protected List<SingularAttribute<M, String>> getColumnsFilterableByString() {
        return ImmutableList.of();
    }
}
