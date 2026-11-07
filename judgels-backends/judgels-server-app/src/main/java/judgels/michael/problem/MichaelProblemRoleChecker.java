package judgels.michael.problem;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.api.catalog.problem.Problem;
import judgels.catalog.problem.ProblemStore;
import judgels.catalog.problem.partner.ProblemPartnerStore;
import judgels.session.Actor;
import judgels.user.role.ProblemAdminRoleChecker;

public class MichaelProblemRoleChecker {
    private final ProblemAdminRoleChecker roleChecker;
    private final ProblemStore problemStore;
    private final ProblemPartnerStore partnerStore;

    @Inject
    public MichaelProblemRoleChecker(
            ProblemAdminRoleChecker roleChecker,
            ProblemStore problemStore,
            ProblemPartnerStore partnerStore) {

        this.roleChecker = roleChecker;
        this.problemStore = problemStore;
        this.partnerStore = partnerStore;
    }

    public boolean isAdmin(Actor actor) {
        return roleChecker.isAdmin(actor);
    }

    public boolean isWriter(Actor actor) {
        return true;
    }

    public boolean canView(Actor actor, Problem problem) {
        return isAuthorOrAbove(actor, problem) || partnerStore.isPartner(problem.getJid(), actor.getUserJid());
    }

    public boolean canEdit(Actor actor, Problem problem) {
        return canView(actor, problem);
    }

    public Optional<String> canSubmit(Actor actor, Problem problem) {
        if (!canEdit(actor, problem)) {
            return Optional.of("Submission not allowed.");
        }
        if (problemStore.userCloneExists(actor.getUserJid(), problem.getJid())) {
            return Optional.of("Submission not allowed if there are local changes.");
        }
        return Optional.empty();
    }

    public boolean isAuthorOrAbove(Actor actor, Problem problem) {
        return isAdmin(actor) || problem.getAuthorJid().equals(actor.getUserJid());
    }
}
