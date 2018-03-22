package judgels.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.metamodel.SingularAttribute;
import judgels.persistence.api.Page;

public interface UnmodifiableDao<M extends UnmodifiableModel> {
    M insert(M model);
    List<M> insertAll(List<M> models);

    Optional<M> select(long id);
    Optional<M> selectByUniqueColumn(SingularAttribute<M, String> column, String value);
    Optional<M> selectByUniqueColumns(Map<SingularAttribute<M, ?>, ?> key);

    long selectCount();
    long selectCountByColumn(SingularAttribute<M, String> column, String value);
    long selectCountByColumns(Map<SingularAttribute<M, ?>, ?> key);

    Page<M> selectAll(int page, int pageSize);
    Page<M> selectAllByColumn(SingularAttribute<M, String> column, String value, int page, int pageSize);
    Page<M> selectAllByColumns(Map<SingularAttribute<M, ?>, ?> key, int page, int pageSize);

    void delete(M model);
}
