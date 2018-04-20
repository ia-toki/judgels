package judgels.uriel.contest.manager;

import javax.inject.Inject;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestManagerModel;

public class ContestManagerStore {
    private final ContestManagerDao managerDao;

    @Inject
    public ContestManagerStore(ContestManagerDao managerDao) {
        this.managerDao = managerDao;
    }

    // temporary
    public void addManager(String contestJid, String userJid) {
        ContestManagerModel model = new ContestManagerModel();
        model.contestJid = contestJid;
        model.userJid = userJid;
        managerDao.insert(model);
    }
}
