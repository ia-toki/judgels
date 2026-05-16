package judgels.contest.editorial;

import jakarta.inject.Inject;
import judgels.api.contest.Contest;
import judgels.contest.ContestTimer;
import judgels.contest.module.ContestModuleStore;

public class ContestEditorialRoleChecker {
    private final ContestTimer contestTimer;
    private final ContestModuleStore moduleStore;

    @Inject
    public ContestEditorialRoleChecker(ContestTimer contestTimer, ContestModuleStore moduleStore) {
        this.contestTimer = contestTimer;
        this.moduleStore = moduleStore;
    }

    public boolean canView(Contest contest) {
        return contestTimer.hasEnded(contest)
                && moduleStore.hasEditorialModule(contest.getJid());
    }
}
