package judgels.persistence;

import java.util.List;
import judgels.persistence.api.dump.Dump;

public interface Dao<M extends Model> extends UnmodifiableDao<M> {
    M update(M model);
    List<M> updateAll(List<M> models);

    void setModelMetadataFromDump(M model, Dump dump);

    @Deprecated void persist(M model, String user, String ipAddress);
    @Deprecated M edit(M model, String user, String ipAddress);
    @Deprecated void remove(M model);
}
