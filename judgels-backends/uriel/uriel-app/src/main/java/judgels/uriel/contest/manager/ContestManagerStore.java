package judgels.uriel.contest.manager;

import com.google.common.collect.Lists;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.api.dump.DumpImportMode;
import judgels.uriel.api.contest.dump.ContestManagerDump;
import judgels.uriel.api.contest.manager.ContestManager;
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

    public void importDump(String contestJid, ContestManagerDump dump) {
        ContestManagerModel model = new ContestManagerModel();
        model.contestJid = contestJid;
        model.userJid = dump.getUserJid();
        managerDao.setModelMetadataFromDump(model, dump);
        managerDao.persist(model);
    }

    public Set<ContestManagerDump> exportDumps(String contestJid, DumpImportMode mode) {
        return managerDao.selectAllByContestJid(contestJid, SelectionOptions.DEFAULT_ALL).stream().map(model -> {
            ContestManagerDump.Builder builder = new ContestManagerDump.Builder()
                    .mode(mode)
                    .userJid(model.userJid);

            if (mode == DumpImportMode.RESTORE) {
                builder
                        .createdAt(model.createdAt)
                        .createdBy(Optional.ofNullable(model.createdBy))
                        .createdIp(Optional.ofNullable(model.createdIp))
                        .updatedAt(model.updatedAt)
                        .updatedBy(Optional.ofNullable(model.updatedBy))
                        .updatedIp(Optional.ofNullable(model.updatedIp));
            }

            return builder.build();
        }).collect(Collectors.toSet());
    }

    private static ContestManager fromModel(ContestManagerModel model) {
        return new ContestManager.Builder()
                .userJid(model.userJid)
                .build();
    }
}
