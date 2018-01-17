package judgels.persistence;

import java.util.Optional;

public interface Dao<M extends Model> {
    M insert(M model);
    Optional<M> select(long id);
    M update(M model);
    void delete(M model);
}
