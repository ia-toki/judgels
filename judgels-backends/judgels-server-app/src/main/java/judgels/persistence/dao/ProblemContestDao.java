package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.List;
import judgels.persistence.DaoData;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.model.ProblemContestModel;
import judgels.persistence.model.ProblemContestModel_;

public class ProblemContestDao extends UnmodifiableDao<ProblemContestModel> {

    @Inject
    public ProblemContestDao(DaoData data) {
        super(data);
    }

    public List<ProblemContestModel> selectAllByProblemJid(String problemJid) {
        return select()
                .where(columnEq(ProblemContestModel_.problemJid, problemJid))
                .orderBy(UnmodifiableModel_.ID, OrderDir.ASC)
                .all();
    }

    public List<ProblemContestModel> selectAllByContestJid(String contestJid) {
        return select()
                .where(columnEq(ProblemContestModel_.contestJid, contestJid))
                .all();
    }
}
