package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.List;
import judgels.api.problem.ProblemSetterRole;
import judgels.persistence.DaoData;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.model.ProblemSetterModel;
import judgels.persistence.model.ProblemSetterModel_;

public class ProblemSetterDao extends UnmodifiableDao<ProblemSetterModel> {
    @Inject
    public ProblemSetterDao(DaoData data) {
        super(data);
    }

    public List<ProblemSetterModel> selectAllByProblemJid(String problemJid) {
        return select()
                .where(columnEq(ProblemSetterModel_.problemJid, problemJid))
                .orderBy(UnmodifiableModel_.ID, OrderDir.ASC)
                .all();
    }

    public List<ProblemSetterModel> selectAllByProblemJidAndRole(String problemJid, ProblemSetterRole role) {
        return select()
                .where(columnEq(ProblemSetterModel_.problemJid, problemJid))
                .where(columnEq(ProblemSetterModel_.role, role.name()))
                .orderBy(UnmodifiableModel_.ID, OrderDir.ASC)
                .all();
    }
}
