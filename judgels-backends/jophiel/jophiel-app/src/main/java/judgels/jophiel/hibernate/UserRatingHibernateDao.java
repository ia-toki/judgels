package judgels.jophiel.hibernate;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.UserRatingDao;
import judgels.jophiel.persistence.UserRatingModel;
import judgels.persistence.ActorProvider;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

@Singleton
public class UserRatingHibernateDao extends UnmodifiableHibernateDao<UserRatingModel> implements UserRatingDao {
    @Inject
    public UserRatingHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Map<String, UserRatingModel> selectAllByTimeAndUserJids(Instant time, Set<String> userJids) {
        // selects the row for each `userJid` with the latest `time` before the given time

        Query<UserRatingModel> query = currentSession().createQuery(
                "SELECT t1 FROM jophiel_user_rating t1 "
                + "LEFT OUTER JOIN jophiel_user_rating t2 "
                + "ON (t1.userJid = t2.userJid AND t1.time < t2.time) "
                + "WHERE t1.time < :time AND t1.userJid IN (:userJids) AND t2.userJid IS NULL",
                UserRatingModel.class);

        query.setParameter("time", time);
        query.setParameterList("userJids", userJids);

        return query.getResultList()
                .stream()
                .collect(Collectors.toMap(m -> m.userJid, m -> m));
    }

    @Override
    public Page<UserRatingModel> selectTopPagedByTime(Instant time, SelectionOptions options) {
        Query<Long> countQuery = currentSession().createQuery(
                "SELECT COUNT(*) FROM jophiel_user_rating t1 "
                        + "LEFT OUTER JOIN jophiel_user_rating t2 "
                        + "ON (t1.userJid = t2.userJid AND t1.time < t2.time) "
                        + "WHERE t1.time < :time AND t2.userJid IS NULL ",
                Long.class);

        countQuery.setParameter("time", time);
        long count = countQuery.getSingleResult();

        Query<UserRatingModel> dataQuery = currentSession().createQuery(
                "SELECT t1 FROM jophiel_user_rating t1 "
                        + "LEFT OUTER JOIN jophiel_user_rating t2 "
                        + "ON (t1.userJid = t2.userJid AND t1.time < t2.time) "
                        + "WHERE t1.time < :time AND t2.userJid IS NULL "
                        + "ORDER BY t1.publicRating DESC",
                UserRatingModel.class);

        dataQuery.setParameter("time", time);
        if (options.getPageSize() > 0) {
            dataQuery.setFirstResult(options.getPageSize() * (options.getPage() - 1));
            dataQuery.setMaxResults(options.getPageSize());
        }

        List<UserRatingModel> data = dataQuery.getResultList();

        return new Page.Builder<UserRatingModel>()
                .data(data)
                .totalData(count)
                .build();
    }
}
