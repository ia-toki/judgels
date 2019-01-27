package judgels.persistence.hibernate;

import com.google.common.collect.Lists;
import java.time.Clock;
import java.util.List;
import javax.persistence.criteria.CriteriaQuery;
import judgels.persistence.ActorProvider;
import judgels.persistence.Dao;
import judgels.persistence.Model;
import org.hibernate.SessionFactory;

public abstract class HibernateDao<M extends Model> extends UnmodifiableHibernateDao<M> implements Dao<M> {
    private final Clock clock;
    private final ActorProvider actorProvider;

    public HibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
        this.clock = clock;
        this.actorProvider = actorProvider;
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
    public void persist(M model, String user, String ipAddress) {
        model.createdBy = user;
        model.createdAt = clock.instant();
        model.createdIp = ipAddress;

        model.updatedBy = model.createdBy;
        model.updatedAt = model.createdAt;
        model.updatedIp = model.createdIp;

        persist(model);
    }

    @Override
    public M edit(M model, String user, String ipAddress) {
        model.updatedBy = user;
        model.updatedAt = clock.instant();
        model.updatedIp = ipAddress;

        return persist(model);
    }

    @Override
    public void remove(M model) {
        delete(model);
    }

    @Deprecated
    protected final M getFirstResultAndDeleteTheRest(CriteriaQuery<M> query) {
        List<M> resultList = currentSession().createQuery(query).getResultList();
        M result = resultList.get(0);

        for (int i = 1; i < resultList.size(); ++i) {
            remove(resultList.get(i));
        }

        return result;
    }
}
