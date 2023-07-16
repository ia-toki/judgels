package judgels.jophiel.hibernate;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import judgels.jophiel.persistence.UserRatingDao;
import judgels.jophiel.persistence.UserRatingModel;
import judgels.jophiel.persistence.UserRatingModel_;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import org.hibernate.query.Query;

public class UserRatingHibernateDao extends UnmodifiableHibernateDao<UserRatingModel> implements UserRatingDao {
    @Inject
    public UserRatingHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<UserRatingModel> selectAllByTimeAndUserJids(Instant time, Collection<String> userJids) {
        if (userJids.isEmpty()) {
            return Collections.emptyList();
        }

        // selects the row for each `userJid` with the latest `time` before the given time

        Query<UserRatingModel> query = currentSession().createQuery(
                "SELECT t1 FROM jophiel_user_rating t1 "
                        + "LEFT OUTER JOIN jophiel_user_rating t2 "
                        + "ON (t1.userJid = t2.userJid AND t1.time < t2.time AND t2.time < :time) "
                        + "WHERE t1.time < :time AND t1.userJid IN :userJids AND t2.userJid IS NULL",
                UserRatingModel.class);

        query.setParameter("time", time);
        query.setParameterList("userJids", userJids);

        return query.getResultList();
    }

    @Override
    public Page<UserRatingModel> selectTopPagedByTime(Instant time, int pageNumber, int pageSize) {
        Query<Long> countQuery = currentSession().createQuery(
                "SELECT COUNT(*) FROM jophiel_user_rating t1 "
                        + "LEFT OUTER JOIN jophiel_user_rating t2 "
                        + "ON (t1.userJid = t2.userJid AND t1.time < t2.time AND t2.time < :time) "
                        + "WHERE t1.time < :time AND t2.userJid IS NULL ",
                Long.class);

        countQuery.setParameter("time", time);
        int count = (int) (long) countQuery.getSingleResult();

        Query<UserRatingModel> dataQuery = currentSession().createQuery(
                "SELECT t1 FROM jophiel_user_rating t1 "
                        + "LEFT OUTER JOIN jophiel_user_rating t2 "
                        + "ON (t1.userJid = t2.userJid AND t1.time < t2.time AND t2.time < :time) "
                        + "WHERE t1.time < :time AND t2.userJid IS NULL "
                        + "ORDER BY t1.publicRating DESC",
                UserRatingModel.class);

        dataQuery.setParameter("time", time);
        dataQuery.setFirstResult(pageSize * (pageNumber - 1));
        dataQuery.setMaxResults(pageSize);

        List<UserRatingModel> page = dataQuery.getResultList();

        return new Page.Builder<UserRatingModel>()
                .page(page)
                .totalCount(count)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();
    }

    @Override
    public List<UserRatingModel> selectAllByUserJid(String userJid) {
        return select().where(columnEq(UserRatingModel_.userJid, userJid)).all();
    }
}
