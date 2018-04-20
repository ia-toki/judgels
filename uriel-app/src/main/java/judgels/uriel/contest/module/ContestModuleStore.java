package judgels.uriel.contest.module;

import javax.inject.Inject;
import judgels.uriel.api.contest.module.ContestModule;
import judgels.uriel.persistence.ContestModuleDao;
import judgels.uriel.persistence.ContestModuleModel;

public class ContestModuleStore {
    private ContestModuleDao moduleDao;

    @Inject
    public ContestModuleStore(ContestModuleDao moduleDao) {
        this.moduleDao = moduleDao;
    }

    // temporary
    public void addModule(String contestJid, ContestModule module) {
        ContestModuleModel model = new ContestModuleModel();
        model.contestJid = contestJid;
        model.name = module.name();
        model.enabled = true;
        model.config = "{}";
        moduleDao.insert(model);
    }
}
