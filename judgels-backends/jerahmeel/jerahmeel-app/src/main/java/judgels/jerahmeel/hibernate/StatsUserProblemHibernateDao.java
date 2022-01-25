package judgels.jerahmeel.hibernate;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import judgels.persistence.FilterOptions;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class StatsUserProblemHibernateDao extends HibernateDao<StatsUserProblemModel> implements StatsUserProblemDao {
    @Inject
    public StatsUserProblemHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<StatsUserProblemModel> selectByUserJidAndProblemJid(String userJid, String problemJid) {
        return selectByUniqueColumns(ImmutableMap.of(
                StatsUserProblemModel_.userJid, userJid,
                StatsUserProblemModel_.problemJid, problemJid));
    }

    @Override
    public List<StatsUserProblemModel> selectAllByUserJidAndProblemJids(String userJid, Set<String> problemJids) {
        return selectAll(new FilterOptions.Builder<StatsUserProblemModel>()
                .putColumnsEq(StatsUserProblemModel_.userJid, userJid)
                .putColumnsIn(StatsUserProblemModel_.problemJid, problemJids)
                .build());
    }

    @Override
    public List<StatsUserProblemModel> selectAllByUserJidsAndProblemJids(
            Set<String> userJids,
            Set<String> problemJids) {
        return selectAll(new FilterOptions.Builder<StatsUserProblemModel>()
                .putColumnsIn(StatsUserProblemModel_.userJid, userJids)
                .putColumnsIn(StatsUserProblemModel_.problemJid, problemJids)
                .build());
    }

    @Override
    public List<StatsUserProblemModel> selectAllByProblemJid(String problemJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<StatsUserProblemModel>()
                .putColumnsEq(StatsUserProblemModel_.problemJid, problemJid)
                .build(), options);
    }

    @Override
    public List<StatsUserProblemModel> selectAllAcceptedByProblemJid(String problemJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<StatsUserProblemModel>()
                .putColumnsEq(StatsUserProblemModel_.problemJid, problemJid)
                .putColumnsEq(StatsUserProblemModel_.verdict, Verdict.ACCEPTED.getCode())
                .build(), options);
    }

    @Override
    public Map<String, Long> selectTotalScoresByProblemJids(Set<String> problemJids) {
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
    public Map<String, Long> selectCountsAcceptedByProblemJids(Set<String> problemJids) {
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
    public Map<String, Long> selectCountsTriedByProblemJids(Set<String> problemJids) {
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
        return selectCount(new FilterOptions.Builder<StatsUserProblemModel>()
                .putColumnsEq(StatsUserProblemModel_.userJid, userJid)
                .build());
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
