package judgels.persistence;

import java.util.Optional;

public interface UnmodifiableDao<M extends UnmodifiableModel> {
    M insert(M model);
    Optional<M> select(long id);
    void delete(M model);
}
