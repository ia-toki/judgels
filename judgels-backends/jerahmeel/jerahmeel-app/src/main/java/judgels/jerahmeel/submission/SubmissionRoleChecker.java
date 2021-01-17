package judgels.jerahmeel.submission;

import javax.inject.Inject;
import judgels.jerahmeel.role.RoleChecker;

public class SubmissionRoleChecker {
    private final RoleChecker roleChecker;

    @Inject
    public SubmissionRoleChecker(RoleChecker roleChecker) {
        this.roleChecker = roleChecker;
    }

    public boolean canViewSource(String userJid, String submissionUserJid) {
        if (roleChecker.isAdmin(userJid)) {
            return true;
        }
        return userJid.equals(submissionUserJid);
    }

    public boolean canManage(String userJid) {
        return roleChecker.isAdmin(userJid);
    }
}
