package judgels.persistence;

import java.time.Clock;

public abstract class Dao<M extends Model> extends UnmodifiableDao<M> {
    private final Clock clock;
    private final ActorProvider actorProvider;

    public Dao(DaoData data) {
        super(data);
        this.clock = data.getClock();
        this.actorProvider = data.getActorProvider();
    }

    @Override
    public M insert(M model) {
        model.createdBy = actorProvider.getJid().orElse(null);
        model.createdAt = clock.instant();
        model.createdIp = actorProvider.getIpAddress().orElse(null);

        model.updatedBy = model.createdBy;
        model.updatedAt = model.createdAt;
        model.updatedIp = model.createdIp;

        return persist(model);
    }

    public M update(M model) {
        model.updatedBy = actorProvider.getJid().orElse(null);
        model.updatedAt = clock.instant();
        model.updatedIp = actorProvider.getIpAddress().orElse(null);

        return persist(model);
    }
}
