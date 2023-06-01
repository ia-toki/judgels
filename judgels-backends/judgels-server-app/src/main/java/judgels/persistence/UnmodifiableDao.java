package judgels.persistence;

import java.util.Optional;
import judgels.persistence.api.dump.UnmodifiableDump;

public interface UnmodifiableDao<M extends UnmodifiableModel> {
    void flush();
    void clear();

    M insert(M model);
    M persist(M model);
    void delete(M model);

    QueryBuilder<M> select();
    Optional<M> selectById(long id);

    void setModelMetadataFromDump(M model, UnmodifiableDump dump);
}
