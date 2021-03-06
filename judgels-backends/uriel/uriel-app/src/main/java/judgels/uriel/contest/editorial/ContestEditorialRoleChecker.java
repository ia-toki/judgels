package judgels.uriel.contest.editorial;

import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.contest.ContestTimer;
import judgels.uriel.contest.module.ContestModuleStore;

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
