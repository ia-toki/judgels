package judgels.persistence;

import static judgels.persistence.CriteriaPredicate.literalFalse;

import io.dropwizard.hibernate.AbstractDAO;
import jakarta.persistence.metamodel.SingularAttribute;
import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

public abstract class UnmodifiableDao<M extends UnmodifiableModel> extends AbstractDAO<M> {
    private final Clock clock;
    private final ActorProvider actorProvider;

    public UnmodifiableDao(DaoData data) {
        super(data.getSessionFactory());
        this.clock = data.getClock();
        this.actorProvider = data.getActorProvider();
    }

    public void flush() {
        currentSession().flush();
    }

    public void clear() {
        currentSession().clear();
    }

    public void delete(M model) {
        currentSession().delete(model);
    }

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

    public QueryBuilder<M> select() {
        return new QueryBuilder<>(currentSession(), getEntityClass());
    }

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

    protected static String escape(String val) {
        if (val == null) {
            return "NULL";
        }
        return "'" + val
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\t", "\\t")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                + "'";
    }

    protected static String escape(Integer val) {
        if (val == null) {
            return "NULL";
        }
        return "" + val;
    }

    protected static String escape(long val) {
        return "" + val;
    }

    protected static String escape(boolean val) {
        return "b'" + (val ? '1' : '0') + "'";
    }

    protected static String escape(Instant val) {
        if (val == null) {
            return "NULL";
        }

        String valString = val.toString().replace("T", " ").replace("Z", "");
        return "'" + valString + "'";
    }
}
