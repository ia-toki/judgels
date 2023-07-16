package judgels.jerahmeel.hibernate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.persistence.Tuple;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.jerahmeel.persistence.ProblemSetProblemModel_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import org.hibernate.query.Query;

public class ProblemSetProblemHibernateDao extends HibernateDao<ProblemSetProblemModel> implements ProblemSetProblemDao {
    @Inject
    public ProblemSetProblemHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public QueryBuilder<ProblemSetProblemModel> selectByProblemSetJid(String problemSetJid) {
        return select()
                .where(columnEq(ProblemSetProblemModel_.problemSetJid, problemSetJid));
    }

    @Override
    public QueryBuilder<ProblemSetProblemModel> selectByProblemSetJids(Collection<String> problemSetJids) {
        return select().where(columnIn(ProblemSetProblemModel_.problemSetJid, problemSetJids));
    }

    @Override
    public List<ProblemSetProblemModel> selectAllByProblemJid(String problemJid) {
        return select().where(columnEq(ProblemSetProblemModel_.problemJid, problemJid)).all();
    }

    @Override
    public List<ProblemSetProblemModel> selectAllByProblemJids(Collection<String> problemJids) {
        return select().where(columnIn(ProblemSetProblemModel_.problemJid, problemJids)).all();
    }

    @Override
    public Optional<ProblemSetProblemModel> selectByProblemSetJidAndProblemJid(String problemSetJid, String problemJid) {
        return select()
                .where(columnEq(ProblemSetProblemModel_.problemSetJid, problemSetJid))
                .where(columnEq(ProblemSetProblemModel_.problemJid, problemJid))
                .unique();
    }

    @Override
    public Optional<ProblemSetProblemModel> selectByProblemSetJidAndProblemAlias(String problemSetJid, String problemAlias) {
        return select()
                .where(columnEq(ProblemSetProblemModel_.problemSetJid, problemSetJid))
                .where(columnEq(ProblemSetProblemModel_.alias, problemAlias))
                .unique();
    }

    @Override
    public Page<ProblemSetProblemModel> selectPagedByDifficulty(
            Set<String> allowedProblemJids,
            int pageNumber,
            int pageSize) {

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

        String orderDir = "DESC";

        countQ = String.format(countQ, where);
        dataQ = String.format(dataQ, where, orderDir, orderDir);

        if (allowedProblemJids == null || !allowedProblemJids.isEmpty()) {
            Query<Long> countQuery = currentSession().createQuery(countQ, Long.class);
            Query<Tuple> dataQuery = currentSession().createQuery(dataQ, Tuple.class);

            if (allowedProblemJids != null && !allowedProblemJids.isEmpty()) {
                countQuery.setParameterList("problemJids", allowedProblemJids);
                dataQuery.setParameterList("problemJids", allowedProblemJids);
            }

            dataQuery.setFirstResult(pageSize * (pageNumber - 1));
            dataQuery.setMaxResults(pageSize);

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
                .totalCount((int) count)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();
    }
}
