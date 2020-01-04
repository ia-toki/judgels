package judgels.jerahmeel.hibernate;

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
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.jerahmeel.persistence.ProblemSetProblemModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.api.problem.ProblemType;

public class ProblemSetProblemHibernateDao extends HibernateDao<ProblemSetProblemModel>
        implements ProblemSetProblemDao {

    @Inject
    public ProblemSetProblemHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ProblemSetProblemModel> selectByProblemJid(String problemJid) {
        return selectByFilter(new FilterOptions.Builder<ProblemSetProblemModel>()
                .putColumnsEq(ProblemSetProblemModel_.problemJid, problemJid)
                .build());
    }

    @Override
    public List<ProblemSetProblemModel> selectAllByProblemJids(Set<String> problemJids) {
        return selectAll(new FilterOptions.Builder<ProblemSetProblemModel>()
                .putColumnsIn(ProblemSetProblemModel_.problemJid, problemJids)
                .build());
    }

    @Override
    public Optional<ProblemSetProblemModel> selectByProblemSetJidAndProblemAlias(
            String problemSetJid,
            String problemAlias) {

        return selectByFilter(new FilterOptions.Builder<ProblemSetProblemModel>()
                .putColumnsEq(ProblemSetProblemModel_.problemSetJid, problemSetJid)
                .putColumnsEq(ProblemSetProblemModel_.alias, problemAlias)
                .build());
    }

    @Override
    public List<ProblemSetProblemModel> selectAllByProblemSetJid(String problemSetJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ProblemSetProblemModel>()
                .putColumnsEq(ProblemSetProblemModel_.problemSetJid, problemSetJid)
                .build(), options);
    }

    @Override
    public Map<String, Long> selectCountsByProblemSetJids(Set<String> problemSetJids) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<ProblemSetProblemModel> root = cq.from(getEntityClass());

        cq.select(cb.tuple(
                root.get(ProblemSetProblemModel_.problemSetJid),
                cb.count(root)));

        cq.where(
                cb.equal(root.get(ProblemSetProblemModel_.status), "VISIBLE"),
                cb.equal(root.get(ProblemSetProblemModel_.type), ProblemType.PROGRAMMING.name()),
                root.get(ProblemSetProblemModel_.problemSetJid).in(problemSetJids));

        cq.groupBy(
                root.get(ProblemSetProblemModel_.problemSetJid));

        return currentSession().createQuery(cq).getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, String.class), tuple -> tuple.get(1, Long.class)));

    }
}
