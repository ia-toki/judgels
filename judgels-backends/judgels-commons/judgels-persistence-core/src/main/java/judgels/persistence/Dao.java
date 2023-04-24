package judgels.persistence;

import judgels.persistence.api.dump.Dump;

public interface Dao<M extends Model> extends UnmodifiableDao<M> {
    M update(M model);

    void setModelMetadataFromDump(M model, Dump dump);
}
