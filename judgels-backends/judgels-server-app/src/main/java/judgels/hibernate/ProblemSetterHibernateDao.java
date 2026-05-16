package judgels.hibernate;

import jakarta.inject.Inject;
import java.util.List;
import judgels.api.problem.ProblemSetterRole;
import judgels.persistence.ProblemSetterDao;
import judgels.persistence.ProblemSetterModel;
import judgels.persistence.ProblemSetterModel_;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;

public class ProblemSetterHibernateDao extends UnmodifiableHibernateDao<ProblemSetterModel> implements ProblemSetterDao {
    @Inject
    public ProblemSetterHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<ProblemSetterModel> selectAllByProblemJid(String problemJid) {
        return select()
                .where(columnEq(ProblemSetterModel_.problemJid, problemJid))
                .orderBy(UnmodifiableModel_.ID, OrderDir.ASC)
                .all();
    }

    @Override
    public List<ProblemSetterModel> selectAllByProblemJidAndRole(String problemJid, ProblemSetterRole role) {
        return select()
                .where(columnEq(ProblemSetterModel_.problemJid, problemJid))
                .where(columnEq(ProblemSetterModel_.role, role.name()))
                .orderBy(UnmodifiableModel_.ID, OrderDir.ASC)
                .all();
    }
}
