package judgels.persistence;

import java.util.List;

public interface Dao<M extends Model> extends UnmodifiableDao<M> {
    M update(M model);
    List<M> updateAll(List<M> models);

    @Deprecated void persist(M model, String user, String ipAddress);
    @Deprecated M edit(M model, String user, String ipAddress);
    @Deprecated void remove(M model);
}
