package judgels.uriel.hibernate;

import java.time.Clock;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.ActorProvider;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.contest.ContestDao;
import judgels.uriel.contest.ContestModel;
import judgels.uriel.contest.ContestModel_;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class ContestHibernateDao extends JudgelsHibernateDao<ContestModel> implements ContestDao {
    public static final String ALLOWED_CONTEST_NAME = "%TOKI Open Contest%";
    public static final String ALLOWED_CONTEST_NAME_EXCEPTION = "%Testing%";

    public ContestHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public long selectCountPublic() {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ContestModel> root = cq.from(getEntityClass());
        cq.where(
                cb.and(
                        cb.like(root.get(ContestModel_.name), ALLOWED_CONTEST_NAME),
                        cb.notLike(root.get(ContestModel_.name), ALLOWED_CONTEST_NAME_EXCEPTION)));
        cq.select(cb.count(root));
        return currentSession().createQuery(cq).getSingleResult();
    }

    @Override
    public Page<ContestModel> selectAllPublic(int page, int pageSize) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ContestModel> cq = cb.createQuery(getEntityClass());
        Root<ContestModel> root = cq.from(getEntityClass());

        cq.where(
                cb.and(
                        cb.like(root.get(ContestModel_.name), ALLOWED_CONTEST_NAME),
                        cb.notLike(root.get(ContestModel_.name), ALLOWED_CONTEST_NAME_EXCEPTION)));

        Query<ContestModel> query = currentSession().createQuery(cq);

        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);

        List<ContestModel> data = query.list();
        long totalData = selectCountPublic();
        long totalPages = (totalData + pageSize - 1) / pageSize;

        return new Page.Builder<ContestModel>()
                .currentPage(page)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .totalData(totalData)
                .data(data)
                .build();
    }
}
