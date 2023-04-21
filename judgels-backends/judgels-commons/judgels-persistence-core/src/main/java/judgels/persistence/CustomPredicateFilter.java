package judgels.persistence;

import java.util.List;
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

    @SafeVarargs
    static <M> CustomPredicateFilter<M> and(CustomPredicateFilter<M>... predicateFilters) {
        return (cb, cq, root) -> {
            Predicate[] predicates = new Predicate[predicateFilters.length];
            for (int i = 0; i < predicateFilters.length; i++) {
                predicates[i] = predicateFilters[i].apply(cb, cq, root);
            }
            return cb.and(predicates);
        };
    }

    static <M> CustomPredicateFilter<M> and(List<CustomPredicateFilter<M>> predicateFilters) {
        return (cb, cq, root) -> {
            Predicate[] predicates = new Predicate[predicateFilters.size()];
            for (int i = 0; i < predicateFilters.size(); i++) {
                predicates[i] = predicateFilters.get(i).apply(cb, cq, root);
            }
            return cb.and(predicates);
        };
    }

    static <M> CustomPredicateFilter<M> not(CustomPredicateFilter<M> predicateFilter) {
        return (cb, cq, root) -> cb.not(predicateFilter.apply(cb, cq, root));
    }

    static <M> CustomPredicateFilter<M> literalTrue() {
        return (cb, cq, root) -> cb.isTrue(cb.literal(true));
    }
}
