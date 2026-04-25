package judgels.jerahmeel.submission;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.gabriel.api.Verdict;
import judgels.gabriel.api.Verdicts;
import judgels.jerahmeel.persistence.StatsUserProblemDao;
import judgels.jerahmeel.persistence.StatsUserProblemModel;
import judgels.jerahmeel.role.RoleChecker;
import judgels.service.actor.Actors;

public class SubmissionRoleChecker {
    private final RoleChecker roleChecker;
    private final StatsUserProblemDao statsUserProblemDao;

    @Inject
    public SubmissionRoleChecker(RoleChecker roleChecker, StatsUserProblemDao statsUserProblemDao) {
        this.roleChecker = roleChecker;
        this.statsUserProblemDao = statsUserProblemDao;
    }

    public Optional<String> canViewProblemSetSource(String userJid, String submissionUserJid, String problemJid) {
        if (Actors.GUEST.equals(userJid)) {
            return Optional.of("Log in to view submission.");
        }
        return Optional.empty();
    }

    public Optional<String> canViewChapterSource(String userJid, String submissionUserJid, String problemJid) {
        if (Actors.GUEST.equals(userJid)) {
            return Optional.of("Log in to view submission.");
        }
        if (roleChecker.isAdmin(userJid)) {
            return Optional.empty();
        }
        if (userJid.equals(submissionUserJid)) {
            return Optional.empty();
        }

        Optional<StatsUserProblemModel> model = statsUserProblemDao.selectByUserJidAndProblemJid(userJid, problemJid);
        if (model.isPresent() && Verdicts.fromCode(model.get().verdict) == Verdict.ACCEPTED) {
            return Optional.empty();
        }
        return Optional.of("You are not allowed to view other submission before solving this problem.");
    }

    public boolean canManage(String userJid) {
        return roleChecker.isAdmin(userJid);
    }
}
