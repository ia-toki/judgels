package judgels.jerahmeel.submission;

import javax.inject.Inject;
import judgels.jerahmeel.persistence.AdminRoleDao;

public class SubmissionRoleChecker {
    private final AdminRoleDao adminRoleDao;

    @Inject
    public SubmissionRoleChecker(AdminRoleDao adminRoleDao) {
        this.adminRoleDao = adminRoleDao;
    }

    public boolean canView(String userJid, String submissionUserJid) {
        if (adminRoleDao.isAdmin(userJid)) {
            return true;
        }
        return userJid.equals(submissionUserJid);
    }

    public boolean canManage(String userJid) {
        return adminRoleDao.isAdmin(userJid);
    }
}
