package judgels.persistence;

import java.util.List;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;

public interface QueryBuilder<M> {
    QueryBuilder<M> where(CriteriaPredicate<M> predicate);
    QueryBuilder<M> orderBy(String column);
    QueryBuilder<M> orderDir(OrderDir dir);
    QueryBuilder<M> orderBy2(String column);
    QueryBuilder<M> orderDir2(OrderDir dir);
    QueryBuilder<M> pageNumber(int pageNumber);
    QueryBuilder<M> pageSize(int pageSize);

    int selectCount();
    List<M> selectAll();
    Page<M> selectPaged();
}
