package judgels.uriel.contest.manager;

import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.contest.ContestRoleChecker;

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
