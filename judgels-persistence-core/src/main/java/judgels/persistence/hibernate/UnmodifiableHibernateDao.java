package judgels.persistence.hibernate;

import io.dropwizard.hibernate.AbstractDAO;
import java.sql.Date;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import judgels.persistence.ActorProvider;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.UnmodifiableModel;
import judgels.persistence.api.Page;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public abstract class UnmodifiableHibernateDao<M extends UnmodifiableModel> extends AbstractDAO<M>
        implements UnmodifiableDao<M> {

    private final Clock clock;
    private final ActorProvider actorProvider;

    public UnmodifiableHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory);
        this.clock = clock;
        this.actorProvider = actorProvider;
    }

    @Override
    public M insert(M model) {
        model.createdBy = actorProvider.getJid().orElse(null);
        model.createdAt = Date.from(clock.instant());
        model.createdIp = actorProvider.getIpAddress().orElse(null);

        return persist(model);
    }

    @Override
    public Optional<M> select(long id) {
        return Optional.ofNullable(get(id));
    }

    @Override
    public long selectCount() {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<M> root = cq.from(getEntityClass());
        cq.select(cb.count(root));
        return currentSession().createQuery(cq).getSingleResult();
    }

    @Override
    public Page<M> selectAll(int page, int pageSize) {
        CriteriaQuery<M> cq = criteriaQuery();
        cq.from(getEntityClass());

        Query<M> query = currentSession().createQuery(cq);

        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);

        List<M> data = query.list();
        long totalData = selectCount();
        long totalPages = (totalData + pageSize - 1) / pageSize;

        return new Page.Builder<M>()
                .currentPage(page)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .totalData(totalData)
                .data(data)
                .build();
    }

    @Override
    public void delete(M model) {
        currentSession().delete(model);
    }

    protected final Optional<M> selectByUniqueColumn(SingularAttribute<M, String> column, String value) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> cq = cb.createQuery(getEntityClass());
        Root<M> root = cq.from(getEntityClass());
        cq.where(cb.equal(root.get(column), value));
        return currentSession().createQuery(cq).uniqueResultOptional();
    }
}
