package judgels.persistence.hibernate;

import static judgels.persistence.CriteriaPredicate.literalFalse;

import io.dropwizard.hibernate.AbstractDAO;
import java.time.Clock;
import java.util.Collection;
import java.util.Optional;
import javax.persistence.metamodel.SingularAttribute;
import judgels.persistence.ActorProvider;
import judgels.persistence.CriteriaPredicate;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.UnmodifiableModel;

public abstract class UnmodifiableHibernateDao<M extends UnmodifiableModel> extends AbstractDAO<M> implements UnmodifiableDao<M> {
    private final Clock clock;
    private final ActorProvider actorProvider;

    public UnmodifiableHibernateDao(HibernateDaoData data) {
        super(data.getSessionFactory());
        this.clock = data.getClock();
        this.actorProvider = data.getActorProvider();
    }

    @Override
    public void flush() {
        currentSession().flush();
    }

    @Override
    public void clear() {
        currentSession().clear();
    }

    @Override
    public void delete(M model) {
        currentSession().delete(model);
    }

    @Override
    public M insert(M model) {
        model.createdBy = actorProvider.getJid().orElse(null);
        model.createdAt = clock.instant();
        model.createdIp = actorProvider.getIpAddress().orElse(null);

        return super.persist(model);
    }

    @Override
    public M persist(M model) {
        return super.persist(model);
    }

    @Override
    public HibernateQueryBuilder<M> select() {
        return new HibernateQueryBuilder<>(currentSession(), getEntityClass());
    }

    @Override
    public Optional<M> selectById(long id) {
        return Optional.ofNullable(get(id));
    }

    protected static <M> CriteriaPredicate<M> columnEq(SingularAttribute<? super M, ?> column, Object value) {
        return (cb, cq, root) -> cb.equal(root.get(column), value);
    }

    protected static <M> CriteriaPredicate<M> columnLike(SingularAttribute<? super M, String> column, String value) {
        return (cb, cq, root) -> cb.like(root.get(column), "%" + value + "%");
    }

    protected static <M> CriteriaPredicate<M> columnIn(SingularAttribute<? super M, ?> column, Collection<?> values) {
        if (values.isEmpty()) {
            // MySQL doesn't support empty IN() clause
            return literalFalse();
        }
        return (cb, cq, root) -> root.get(column).in(values);
    }
}
