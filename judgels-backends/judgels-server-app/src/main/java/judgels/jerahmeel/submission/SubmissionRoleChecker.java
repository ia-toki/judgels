package judgels.jerahmeel.submission;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.gabriel.api.Verdict;
import judgels.gabriel.api.Verdicts;
import judgels.jerahmeel.persistence.StatsUserProblemDao;
import judgels.jerahmeel.persistence.StatsUserProblemModel;
import judgels.jerahmeel.role.RoleChecker;

public class SubmissionRoleChecker {
    private final RoleChecker roleChecker;
    private final StatsUserProblemDao statsUserProblemDao;

    @Inject
    public SubmissionRoleChecker(RoleChecker roleChecker, StatsUserProblemDao statsUserProblemDao) {
        this.roleChecker = roleChecker;
        this.statsUserProblemDao = statsUserProblemDao;
    }

    public boolean canViewProblemSetSource(String userJid, String submissionUserJid, String problemJid) {
        if (roleChecker.isAdmin(userJid)) {
            return true;
        }
        return userJid.equals(submissionUserJid);
    }

    public boolean canViewChapterSource(String userJid, String submissionUserJid, String problemJid) {
        if (roleChecker.isAdmin(userJid)) {
            return true;
        }
        if (userJid.equals(submissionUserJid)) {
            return true;
        }

        Optional<StatsUserProblemModel> model = statsUserProblemDao.selectByUserJidAndProblemJid(userJid, problemJid);
        return model.isPresent() && Verdicts.fromCode(model.get().verdict) == Verdict.ACCEPTED;
    }

    public boolean canManage(String userJid) {
        return roleChecker.isAdmin(userJid);
    }
}
