package judgels.jerahmeel.hibernate;

import java.util.List;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.ProblemContestDao;
import judgels.jerahmeel.persistence.ProblemContestModel;
import judgels.jerahmeel.persistence.ProblemContestModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;
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
        return selectAll(new FilterOptions.Builder<ProblemContestModel>()
                .putColumnsEq(ProblemContestModel_.problemJid, problemJid)
                .build(), new SelectionOptions.Builder().orderDir(OrderDir.ASC).build());
    }

    @Override
    public List<ProblemContestModel> selectAllByContestJid(String contestJid) {
        return selectAll(new FilterOptions.Builder<ProblemContestModel>()
                .putColumnsEq(ProblemContestModel_.contestJid, contestJid)
                .build());
    }
}
