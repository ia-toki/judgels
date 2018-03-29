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

    long selectCount(SelectCountOptions<M> options);
    Page<M> selectAll(SelectAllOptions<M> options);

    void delete(M model);
}
