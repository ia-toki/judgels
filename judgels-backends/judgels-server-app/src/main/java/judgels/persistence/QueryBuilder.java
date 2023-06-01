package judgels.persistence;

import java.util.List;
import java.util.Optional;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;

public interface QueryBuilder<M> {
    QueryBuilder<M> where(CriteriaPredicate<M> predicate);
    QueryBuilder<M> orderBy(String column, OrderDir dir);

    int count();
    Optional<M> unique();
    Optional<M> latest();
    List<M> all();
    Page<M> paged(int pageNumber, int pageSize);
}
