package judgels.uriel.contest.supervisor;

import javax.inject.Inject;
import judgels.uriel.persistence.ContestSupervisorDao;
import judgels.uriel.persistence.ContestSupervisorModel;

public class ContestSupervisorStore {
    private final ContestSupervisorDao supervisorDao;

    @Inject
    public ContestSupervisorStore(ContestSupervisorDao supervisorDao) {
        this.supervisorDao = supervisorDao;
    }

    // temporary
    public void addSupervisor(String contestJid, String userJid) {
        ContestSupervisorModel model = new ContestSupervisorModel();
        model.contestJid = contestJid;
        model.userJid = userJid;
        supervisorDao.insert(model);
    }
}
