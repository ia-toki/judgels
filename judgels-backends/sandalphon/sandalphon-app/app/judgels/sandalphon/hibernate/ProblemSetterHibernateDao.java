package judgels.sandalphon.hibernate;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import judgels.sandalphon.api.problem.ProblemSetterRole;
import judgels.sandalphon.persistence.ProblemSetterDao;
import judgels.sandalphon.persistence.ProblemSetterModel;
import judgels.sandalphon.persistence.ProblemSetterModel_;

@Singleton
public class ProblemSetterHibernateDao extends UnmodifiableHibernateDao<ProblemSetterModel> implements
        ProblemSetterDao {
    @Inject
    public ProblemSetterHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<ProblemSetterModel> selectAllByProblemJid(String problemJid) {
        return selectAll(new FilterOptions.Builder<ProblemSetterModel>()
                .putColumnsEq(ProblemSetterModel_.problemJid, problemJid)
                .build(), new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_ALL)
                .orderDir(OrderDir.ASC)
                .build());
    }

    @Override
    public List<ProblemSetterModel> selectAllByProblemJidAndRole(String problemJid, ProblemSetterRole role) {
        return selectAll(new FilterOptions.Builder<ProblemSetterModel>()
                .putColumnsEq(ProblemSetterModel_.problemJid, problemJid)
                .putColumnsEq(ProblemSetterModel_.role, role.name())
                .build(), new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_ALL)
                .orderDir(OrderDir.ASC)
                .build());
    }
}
