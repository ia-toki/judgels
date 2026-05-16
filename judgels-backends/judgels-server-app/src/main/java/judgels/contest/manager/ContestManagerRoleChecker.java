package judgels.contest.manager;

import jakarta.inject.Inject;
import judgels.api.contest.Contest;
import judgels.contest.ContestRoleChecker;

public class ContestManagerRoleChecker {
    private final ContestRoleChecker contestRoleChecker;

    @Inject
    public ContestManagerRoleChecker(ContestRoleChecker contestRoleChecker) {
        this.contestRoleChecker = contestRoleChecker;
    }

    public boolean canView(String userJid, Contest contest) {
        return contestRoleChecker.canManage(userJid, contest);
    }

    public boolean canManage(String userJid) {
        return contestRoleChecker.canAdminister(userJid);
    }
}
