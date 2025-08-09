package judgels.uriel.contest.manager;

import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.api.Page;
import judgels.uriel.api.contest.manager.ContestManager;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestManagerModel;

public class ContestManagerStore {
    private final ContestManagerDao managerDao;

    @Inject
    public ContestManagerStore(ContestManagerDao managerDao) {
        this.managerDao = managerDao;
    }

    public boolean upsertManager(String contestJid, String userJid) {
        Optional<ContestManagerModel> maybeModel = managerDao.selectByContestJidAndUserJid(contestJid, userJid);
        if (maybeModel.isPresent()) {
            return false;
        }
        ContestManagerModel model = new ContestManagerModel();
        model.contestJid = contestJid;
        model.userJid = userJid;
        managerDao.insert(model);
        return true;
    }

    public boolean deleteManager(String contestJid, String userJid) {
        Optional<ContestManagerModel> maybeModel = managerDao.selectByContestJidAndUserJid(contestJid, userJid);
        if (!maybeModel.isPresent()) {
            return false;
        }
        managerDao.delete(maybeModel.get());
        return true;
    }

    public Page<ContestManager> getManagers(String contestJid, int pageNumber, int pageSize) {
        return managerDao
                .selectByContestJid(contestJid)
                .paged(pageNumber, pageSize)
                .mapPage(p -> Lists.transform(p, ContestManagerStore::fromModel));
    }

    private static ContestManager fromModel(ContestManagerModel model) {
        return new ContestManager.Builder()
                .userJid(model.userJid)
                .build();
    }
}
