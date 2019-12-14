package judgels.jerahmeel.hibernate;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.jerahmeel.persistence.ProblemSetProblemModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

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
}
