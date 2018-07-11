package judgels.persistence;

import java.util.List;

public interface Dao<M extends Model> extends UnmodifiableDao<M> {
    M update(M model);
    List<M> updateAll(List<M> models);
}
