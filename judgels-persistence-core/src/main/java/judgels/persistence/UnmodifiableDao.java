package judgels.persistence;

import java.util.Optional;
import judgels.persistence.api.Page;

public interface UnmodifiableDao<M extends UnmodifiableModel> {
    M insert(M model);
    Optional<M> select(long id);
    long selectCount();
    Page<M> selectAll(int page, int pageSize);
    void delete(M model);
}
