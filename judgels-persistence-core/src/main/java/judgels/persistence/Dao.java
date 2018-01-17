package judgels.persistence;

public interface Dao<M extends Model> extends UnmodifiableDao<M> {
    M update(M model);
}
