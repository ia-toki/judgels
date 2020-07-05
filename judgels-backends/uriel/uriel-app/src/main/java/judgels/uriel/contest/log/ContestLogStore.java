package judgels.uriel.contest.log;

import javax.inject.Inject;
import judgels.uriel.api.contest.log.ContestLog;
import judgels.uriel.persistence.ContestLogDao;
import judgels.uriel.persistence.ContestLogModel;

public class ContestLogStore {
    private final ContestLogDao dao;

    @Inject
    public ContestLogStore(ContestLogDao dao) {
        this.dao = dao;
    }

    public void createLog(ContestLog log) {
        ContestLogModel model = new ContestLogModel();
        model.contestJid = log.getContestJid();
        model.event = log.getEvent();
        model.object = log.getObject().orElse(null);
        model.problemJid = log.getProblemJid().orElse(null);
        model.createdBy = log.getUserJid();
        model.createdAt = log.getTime();
        model.createdIp = log.getIpAddress().orElse(null);

        dao.persist(model);
    }
}
