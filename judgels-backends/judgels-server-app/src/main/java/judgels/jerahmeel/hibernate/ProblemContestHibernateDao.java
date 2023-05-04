package judgels.jerahmeel.hibernate;

import java.util.List;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.ProblemContestDao;
import judgels.jerahmeel.persistence.ProblemContestModel;
import judgels.jerahmeel.persistence.ProblemContestModel_;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;

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
