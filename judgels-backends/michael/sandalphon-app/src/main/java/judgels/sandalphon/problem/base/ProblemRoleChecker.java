package judgels.sandalphon.problem.base;

import javax.inject.Inject;
import judgels.jophiel.api.actor.Actor;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.role.RoleChecker;

public class ProblemRoleChecker {
    private final RoleChecker roleChecker;
    private final ProblemStore problemStore;

    @Inject
    public ProblemRoleChecker(RoleChecker roleChecker, ProblemStore problemStore) {
        this.roleChecker = roleChecker;
        this.problemStore = problemStore;
    }

    public boolean canView(Actor actor, Problem problem) {
        return isPartnerOrAbove(actor, problem);
    }

    public boolean canEdit(Actor actor, Problem problem) {
        return isAuthorOrAbove(actor, problem);
    }

    private boolean isAuthor(Actor actor, Problem problem) {
        return problem.getAuthorJid().equals(actor.getUserJid());
    }

    private boolean isAuthorOrAbove(Actor actor, Problem problem) {
        return roleChecker.isAdmin(actor) || isAuthor(actor, problem);
    }

    private boolean isPartner(Actor actor, Problem problem) {
        return problemStore.isUserPartnerForProblem(problem.getJid(), actor.getUserJid());
    }

    private boolean isPartnerOrAbove(Actor actor, Problem problem) {
        return isAuthorOrAbove(actor, problem) || isPartner(actor, problem);
    }
}
