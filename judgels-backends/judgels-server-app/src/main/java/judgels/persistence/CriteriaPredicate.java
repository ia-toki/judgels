package judgels.persistence;

import java.util.Arrays;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface CriteriaPredicate<M> {
    Predicate apply(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<M> root);

    static <M> CriteriaPredicate<M> literalTrue() {
        return (cb, cq, root) -> cb.isTrue(cb.literal(true));
    }

    static <M> CriteriaPredicate<M> literalFalse() {
        return (cb, cq, root) -> cb.isTrue(cb.literal(false));
    }

    @SafeVarargs
    static <M> CriteriaPredicate<M> or(CriteriaPredicate<M>... predicates) {
        return (cb, cq, root) -> cb.or(
                Arrays.stream(predicates).map(p -> p.apply(cb, cq, root)).toArray(Predicate[]::new));
    }

    @SafeVarargs
    static <M> CriteriaPredicate<M> and(CriteriaPredicate<M>... predicates) {
        return (cb, cq, root) -> cb.and(
                Arrays.stream(predicates).map(p -> p.apply(cb, cq, root)).toArray(Predicate[]::new));
    }

    static <M> CriteriaPredicate<M> and(List<CriteriaPredicate<M>> predicates) {
        return (cb, cq, root) -> cb.and(
                predicates.stream().map(p -> p.apply(cb, cq, root)).toArray(Predicate[]::new));
    }

    static <M> CriteriaPredicate<M> not(CriteriaPredicate<M> predicate) {
        return (cb, cq, root) -> cb.not(predicate.apply(cb, cq, root));
    }
}
