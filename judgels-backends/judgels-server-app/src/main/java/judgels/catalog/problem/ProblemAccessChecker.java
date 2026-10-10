package judgels.catalog.problem;

import static judgels.core.JudgelsRequestChecks.checkAllowed;
import static judgels.core.JudgelsRequestChecks.checkFound;

import jakarta.inject.Inject;
import judgels.api.catalog.problem.Problem;
import judgels.core.api.AuthHeader;
import judgels.session.ActorChecker;

/**
 * Guards the endpoints that read or write a problem's files.
 *
 * A problem's files are edited in the actor's own clone, which is created on their first write.
 * Every endpoint that writes them must go through {@link #checkCanEdit}, so that none writes to the origin.
 */
public class ProblemAccessChecker {
    private final ActorChecker actorChecker;
    private final ProblemRoleChecker roleChecker;
    private final ProblemStore problemStore;

    @Inject
    public ProblemAccessChecker(ActorChecker actorChecker, ProblemRoleChecker roleChecker, ProblemStore problemStore) {
        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.problemStore = problemStore;
    }

    public String checkCanView(AuthHeader authHeader, String problemJid) {
        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFound(problemStore.getProblemByJid(problemJid));
        checkAllowed(roleChecker.canView(actorJid, problem));
        return actorJid;
    }

    public String checkCanEdit(AuthHeader authHeader, String problemJid) {
        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFound(problemStore.getProblemByJid(problemJid));
        checkAllowed(roleChecker.canEdit(actorJid, problem));

        problemStore.createUserCloneIfNotExists(actorJid, problemJid);
        return actorJid;
    }
}
