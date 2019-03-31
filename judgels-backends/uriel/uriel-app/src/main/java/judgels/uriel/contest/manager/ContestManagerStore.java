package judgels.uriel.contest.manager;

import com.google.common.collect.Lists;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.api.dump.DumpImportMode;
import judgels.uriel.api.contest.manager.ContestManager;
import judgels.uriel.api.dump.ContestManagerDump;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestManagerModel;

public class ContestManagerStore {
    private static final int PAGE_SIZE = 250;

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
        SelectionOptions.Builder options = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .pageSize(PAGE_SIZE);
        page.ifPresent(options::page);
        return managerDao.selectPagedByContestJid(contestJid, options.build()).mapPage(
                p -> Lists.transform(p, ContestManagerStore::fromModel));
    }

    public void importDump(String contestJid, ContestManagerDump contestManagerDump) {
        ContestManagerModel contestManagerModel = new ContestManagerModel();
        contestManagerModel.contestJid = contestJid;
        contestManagerModel.userJid = contestManagerDump.getUserJid();
        managerDao.setModelMetadataFromDump(contestManagerModel, contestManagerDump);
        managerDao.persist(contestManagerModel);
    }

    public Set<ContestManagerDump> exportDumps(String contestJid) {
        return managerDao.selectAllByContestJid(contestJid, SelectionOptions.DEFAULT_ALL).stream()
                .map(contestManagerModel -> new ContestManagerDump.Builder()
                        .mode(DumpImportMode.RESTORE)
                        .userJid(contestManagerModel.userJid)
                        .createdAt(contestManagerModel.createdAt)
                        .createdBy(Optional.ofNullable(contestManagerModel.createdBy))
                        .createdIp(Optional.ofNullable(contestManagerModel.createdIp))
                        .updatedAt(contestManagerModel.updatedAt)
                        .updatedBy(Optional.ofNullable(contestManagerModel.updatedBy))
                        .updatedIp(Optional.ofNullable(contestManagerModel.updatedIp))
                        .build())
                .collect(Collectors.toSet());
    }

    private static ContestManager fromModel(ContestManagerModel model) {
        return new ContestManager.Builder()
                .userJid(model.userJid)
                .build();
    }
}
