package judgels.persistence.hibernate;

import com.google.common.collect.Lists;
import java.time.Clock;
import java.util.List;
import judgels.persistence.ActorProvider;
import judgels.persistence.Dao;
import judgels.persistence.Model;
import judgels.persistence.api.dump.Dump;
import judgels.persistence.api.dump.DumpImportMode;

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

    @Override
    public List<M> updateAll(List<M> models) {
        return Lists.transform(models, this::update);
    }

    @Override
    public void setModelMetadataFromDump(M model, Dump dump) {
        super.setModelMetadataFromDump(model, dump);

        if (dump.getMode() == DumpImportMode.RESTORE) {
            model.updatedBy = dump.getUpdatedBy().orElse(null);
            model.updatedIp = dump.getUpdatedIp().orElse(null);
            model.updatedAt = dump.getUpdatedAt().orElseThrow(
                    () -> new IllegalArgumentException("updatedAt must be set if using RESTORE mode")
            );
        } else if (dump.getMode() == DumpImportMode.CREATE) {
            model.updatedBy = model.createdBy;
            model.updatedIp = model.createdIp;
            model.updatedAt = model.createdAt;
        } else {
            throw new IllegalArgumentException(
                    String.format("Unknown mode: %s", dump.getMode())
            );
        }
    }
}
