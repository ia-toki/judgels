package judgels.persistence;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface CustomPredicateFilter<M> {
    Predicate apply(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<M> root);
}
