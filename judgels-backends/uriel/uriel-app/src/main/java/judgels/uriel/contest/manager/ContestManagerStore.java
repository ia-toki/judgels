package judgels.uriel.contest.manager;

import com.google.common.collect.Lists;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
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

    public Page<ContestManager> getManagers(String contestJid, Optional<Integer> page) {
        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(options::page);
        return managerDao.selectPagedByContestJid(contestJid, options.build()).mapPage(
                p -> Lists.transform(p, ContestManagerStore::fromModel));
    }

    private static ContestManager fromModel(ContestManagerModel model) {
        return new ContestManager.Builder()
                .userJid(model.userJid)
                .build();
    }
}
