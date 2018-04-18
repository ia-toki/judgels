package judgels.persistence;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface CustomPredicateFilter<M> {
    Predicate apply(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<M> root);

    @SafeVarargs
    static <M> CustomPredicateFilter<M> or(CustomPredicateFilter<M>... predicateFilters) {
        return (cb, cq, root) -> {
            Predicate[] predicates = new Predicate[predicateFilters.length];
            for (int i = 0; i < predicateFilters.length; i++) {
                predicates[i] = predicateFilters[i].apply(cb, cq, root);
            }
            return cb.or(predicates);
        };
    }

    static <M> CustomPredicateFilter<M> not(CustomPredicateFilter<M> predicateFilter) {
        return (cb, cq, root) -> cb.not(predicateFilter.apply(cb, cq, root));
    }

    static CustomPredicateFilter<?> literalTrue() {
        return (cb, cq, root) -> cb.isTrue(cb.literal(true));
    }
}
