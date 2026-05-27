package judgels.persistence.dao;

import judgels.persistence.model.Model;

public interface Dao<M extends Model> extends UnmodifiableDao<M> {
    M update(M model);
}
