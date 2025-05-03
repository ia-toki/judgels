package judgels.persistence;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;

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
