package judgels.persistence.hibernate;

import java.time.Clock;
import judgels.persistence.ActorProvider;
import judgels.persistence.Dao;
import judgels.persistence.Model;

public abstract class HibernateDao<M extends Model> extends UnmodifiableHibernateDao<M> implements Dao<M> {
    private final Clock clock;
    private final ActorProvider actorProvider;

    public HibernateDao(HibernateDaoData data) {
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

    @Override
    public M update(M model) {
        model.updatedBy = actorProvider.getJid().orElse(null);
        model.updatedAt = clock.instant();
        model.updatedIp = actorProvider.getIpAddress().orElse(null);

        return persist(model);
    }
}
