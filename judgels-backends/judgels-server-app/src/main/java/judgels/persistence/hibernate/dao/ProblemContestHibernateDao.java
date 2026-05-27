package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import java.util.List;
import judgels.persistence.api.OrderDir;
import judgels.persistence.dao.ProblemContestDao;
import judgels.persistence.model.ProblemContestModel;
import judgels.persistence.model.ProblemContestModel_;
import judgels.persistence.model.UnmodifiableModel_;

public class ProblemContestHibernateDao extends UnmodifiableHibernateDao<ProblemContestModel>
        implements ProblemContestDao {

    @Inject
    public ProblemContestHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<ProblemContestModel> selectAllByProblemJid(String problemJid) {
        return select()
                .where(columnEq(ProblemContestModel_.problemJid, problemJid))
                .orderBy(UnmodifiableModel_.ID, OrderDir.ASC)
                .all();
    }

    @Override
    public List<ProblemContestModel> selectAllByContestJid(String contestJid) {
        return select()
                .where(columnEq(ProblemContestModel_.contestJid, contestJid))
                .all();
    }
}
