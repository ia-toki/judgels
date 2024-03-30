package judgels.jerahmeel.hibernate;

import java.time.Clock;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.gabriel.api.Verdict;
import judgels.jerahmeel.persistence.StatsUserProblemDao;
import judgels.jerahmeel.persistence.StatsUserProblemModel;
import judgels.jerahmeel.persistence.StatsUserProblemModel_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class StatsUserProblemHibernateDao extends HibernateDao<StatsUserProblemModel> implements StatsUserProblemDao {
    private final Clock clock;

    @Inject
    public StatsUserProblemHibernateDao(HibernateDaoData data) {
        super(data);
        this.clock = data.getClock();
    }

    @Override
    public StatsUserProblemModel insert(StatsUserProblemModel model) {
        model.createdAt = clock.instant();
        model.updatedAt = model.createdAt;
        return super.persist(model);
    }

    @Override
    public StatsUserProblemModel update(StatsUserProblemModel model) {
        model.updatedAt = clock.instant();
        return super.persist(model);
    }

    @Override
    public Optional<StatsUserProblemModel> selectByUserJidAndProblemJid(String userJid, String problemJid) {
        return select()
                .where(columnEq(StatsUserProblemModel_.userJid, userJid))
                .where(columnEq(StatsUserProblemModel_.problemJid, problemJid))
                .unique();
    }

    @Override
    public List<StatsUserProblemModel> selectAllByUserJidAndProblemJids(String userJid, Collection<String> problemJids) {
        return select()
                .where(columnEq(StatsUserProblemModel_.userJid, userJid))
                .where(columnIn(StatsUserProblemModel_.problemJid, problemJids))
                .all();
    }

    @Override
    public List<StatsUserProblemModel> selectAllByUserJidsAndProblemJids(Collection<String> userJids, Collection<String> problemJids) {
        return select()
                .where(columnIn(StatsUserProblemModel_.userJid, userJids))
                .where(columnIn(StatsUserProblemModel_.problemJid, problemJids))
                .all();
    }

    @Override
    public QueryBuilder<StatsUserProblemModel> selectByProblemJid(String problemJid) {
        return select()
                .where(columnEq(StatsUserProblemModel_.problemJid, problemJid));
    }

    @Override
    public QueryBuilder<StatsUserProblemModel> selectAcceptedByProblemJid(String problemJid) {
        return select()
                .where(columnEq(StatsUserProblemModel_.problemJid, problemJid))
                .where(columnEq(StatsUserProblemModel_.verdict, Verdict.ACCEPTED.getCode()));
    }

    @Override
    public Map<String, Long> selectTotalScoresByProblemJids(Collection<String> problemJids) {
        if (problemJids.isEmpty()) {
            return Collections.emptyMap();
        }

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<StatsUserProblemModel> root = cq.from(getEntityClass());

        cq.select(cb.tuple(
                root.get(StatsUserProblemModel_.problemJid),
                cb.sum(root.get(StatsUserProblemModel_.score))));

        cq.where(
                root.get(StatsUserProblemModel_.problemJid).in(problemJids));

        cq.groupBy(
                root.get(StatsUserProblemModel_.problemJid));

        return currentSession().createQuery(cq).getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, String.class), tuple -> tuple.get(1, Long.class)));
    }

    @Override
    public Map<String, Long> selectCountsAcceptedByProblemJids(Collection<String> problemJids) {
        if (problemJids.isEmpty()) {
            return Collections.emptyMap();
        }

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<StatsUserProblemModel> root = cq.from(getEntityClass());

        cq.select(cb.tuple(
                root.get(StatsUserProblemModel_.problemJid),
                cb.count(root)));

        cq.where(
                cb.equal(root.get(StatsUserProblemModel_.verdict), Verdict.ACCEPTED.getCode()),
                root.get(StatsUserProblemModel_.problemJid).in(problemJids));

        cq.groupBy(
                root.get(StatsUserProblemModel_.problemJid));

        return currentSession().createQuery(cq).getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, String.class), tuple -> tuple.get(1, Long.class)));
    }

    @Override
    public Map<String, Long> selectCountsTriedByProblemJids(Collection<String> problemJids) {
        if (problemJids.isEmpty()) {
            return Collections.emptyMap();
        }

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<StatsUserProblemModel> root = cq.from(getEntityClass());

        cq.select(cb.tuple(
                root.get(StatsUserProblemModel_.problemJid),
                cb.count(root)));

        cq.where(
                root.get(StatsUserProblemModel_.problemJid).in(problemJids));

        cq.groupBy(
                root.get(StatsUserProblemModel_.problemJid));

        return currentSession().createQuery(cq).getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, String.class), tuple -> tuple.get(1, Long.class)));
    }

    @Override
    public long selectCountTriedByUserJid(String userJid) {
        return select()
                .where(columnEq(StatsUserProblemModel_.userJid, userJid))
                .count();
    }

    @Override
    public Map<String, Long> selectCountsVerdictByUserJid(String userJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<StatsUserProblemModel> root = cq.from(getEntityClass());

        cq.select(cb.tuple(
                root.get(StatsUserProblemModel_.verdict),
                cb.count(root)));

        cq.where(
                cb.equal(root.get(StatsUserProblemModel_.userJid), userJid));

        cq.groupBy(
                root.get(StatsUserProblemModel_.verdict));

        return currentSession().createQuery(cq).getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, String.class), tuple -> tuple.get(1, Long.class)));
    }
}
