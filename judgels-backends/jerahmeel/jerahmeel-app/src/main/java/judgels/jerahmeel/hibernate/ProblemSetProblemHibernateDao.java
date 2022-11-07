package judgels.jerahmeel.hibernate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.persistence.Tuple;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.jerahmeel.persistence.ProblemSetProblemModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import org.hibernate.query.Query;

public class ProblemSetProblemHibernateDao extends HibernateDao<ProblemSetProblemModel>
        implements ProblemSetProblemDao {

    @Inject
    public ProblemSetProblemHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<ProblemSetProblemModel> selectAllByProblemJid(String problemJid) {
        return selectAll(new FilterOptions.Builder<ProblemSetProblemModel>()
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
    public Optional<ProblemSetProblemModel> selectByProblemSetJidAndProblemJid(
            String problemSetJid,
            String problemJid) {

        return selectByFilter(new FilterOptions.Builder<ProblemSetProblemModel>()
                .putColumnsEq(ProblemSetProblemModel_.problemSetJid, problemSetJid)
                .putColumnsEq(ProblemSetProblemModel_.problemJid, problemJid)
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
    public List<ProblemSetProblemModel> selectAllByProblemSetJids(
            Set<String> problemSetJids,
            SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ProblemSetProblemModel>()
                .putColumnsIn(ProblemSetProblemModel_.problemSetJid, problemSetJids)
                .build(), options);
    }

    @Override
    public Page<ProblemSetProblemModel> selectPagedByDifficulty(
            Set<String> allowedProblemJids,
            SelectionOptions options) {

        long count = 0;
        List<Tuple> data = ImmutableList.of();

        String countQ = ""
                + "SELECT COUNT(*) FROM jerahmeel_problem_set_problem a "
                + "WHERE type='PROGRAMMING' "
                + "AND %s ";

        String dataQ = ""
                + "SELECT a.problemSetJid, a.problemJid, a.alias, a.type, SUM(s.score), COUNT(s.userJid) "
                + "FROM jerahmeel_problem_set_problem a "
                + "LEFT JOIN jerahmeel_stats_user_problem s "
                + "ON a.problemJid=s.problemJid "
                + "WHERE type='PROGRAMMING' "
                + "AND %s "
                + "GROUP BY a.problemSetJid, a.problemJid "
                + "ORDER BY SUM(s.score) %s, COUNT(s.userJid) %s";

        String where = "1=1";
        if (allowedProblemJids != null) {
            where = "a.problemJid IN :problemJids";
        }

        String orderDir = options.getOrderDir().name();

        countQ = String.format(countQ, where);
        dataQ = String.format(dataQ, where, orderDir, orderDir);

        if (allowedProblemJids == null || !allowedProblemJids.isEmpty()) {
            Query<Long> countQuery = currentSession().createQuery(countQ, Long.class);
            Query<Tuple> dataQuery = currentSession().createQuery(dataQ, Tuple.class);

            if (allowedProblemJids != null && !allowedProblemJids.isEmpty()) {
                countQuery.setParameterList("problemJids", allowedProblemJids);
                dataQuery.setParameterList("problemJids", allowedProblemJids);
            }

            if (options.getPageSize() > 0) {
                dataQuery.setFirstResult(options.getPageSize() * (options.getPage() - 1));
                dataQuery.setMaxResults(options.getPageSize());
            }

            count = countQuery.getSingleResult();
            data = dataQuery.getResultList();
        }

        List<ProblemSetProblemModel> page = Lists.transform(data, t -> {
            ProblemSetProblemModel m = new ProblemSetProblemModel();
            m.problemSetJid = t.get(0, String.class);
            m.problemJid = t.get(1, String.class);
            m.alias = t.get(2, String.class);
            m.type = t.get(3, String.class);
            return m;
        });

        return new Page.Builder<ProblemSetProblemModel>()
                .page(page)
                .totalCount(count)
                .build();
    }
}
