package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import java.util.List;
import judgels.api.problem.ProblemSetterRole;
import judgels.persistence.api.OrderDir;
import judgels.persistence.dao.ProblemSetterDao;
import judgels.persistence.model.ProblemSetterModel;
import judgels.persistence.model.ProblemSetterModel_;
import judgels.persistence.model.UnmodifiableModel_;

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
