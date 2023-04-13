package judgels.sandalphon.problem.base;

import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.actor.Actor;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.resource.Partner;
import judgels.sandalphon.api.resource.PartnerPermission;
import judgels.sandalphon.problem.base.partner.ProblemPartnerStore;
import judgels.sandalphon.role.RoleChecker;

public class ProblemRoleChecker {
    private final RoleChecker roleChecker;
    private final ProblemStore problemStore;
    private final ProblemPartnerStore partnerStore;

    @Inject
    public ProblemRoleChecker(RoleChecker roleChecker, ProblemStore problemStore, ProblemPartnerStore partnerStore) {
        this.roleChecker = roleChecker;
        this.problemStore = problemStore;
        this.partnerStore = partnerStore;
    }

    public boolean isAdmin(Actor actor) {
        return roleChecker.isAdmin(actor);
    }

    public boolean isWriter(Actor actor) {
        return roleChecker.isWriter(actor);
    }

    public boolean canView(Actor actor, Problem problem) {
        return isAuthorOrAbove(actor, problem)
                || isPartner(actor, problem);
    }

    public boolean canEdit(Actor actor, Problem problem) {
        return isAuthorOrAbove(actor, problem)
                || isPartnerWithUpdatePermission(actor, problem);
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

    public boolean isAuthor(Actor actor, Problem problem) {
        return problem.getAuthorJid().equals(actor.getUserJid());
    }

    private boolean isAuthorOrAbove(Actor actor, Problem problem) {
        return roleChecker.isAdmin(actor) || isAuthor(actor, problem);
    }

    private boolean isPartner(Actor actor, Problem problem) {
        Optional<Partner> partner = partnerStore.getPartner(problem.getJid(), actor.getUserJid());
        return partner.isPresent();
    }

    private boolean isPartnerWithUpdatePermission(Actor actor, Problem problem) {
        Optional<Partner> partner = partnerStore.getPartner(problem.getJid(), actor.getUserJid());
        return partner.isPresent() && partner.get().getPermission() == PartnerPermission.UPDATE;
    }
}
