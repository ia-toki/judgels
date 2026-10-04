package judgels.problem;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.api.catalog.Partner;
import judgels.api.catalog.PartnerPermission;
import judgels.api.problem.Problem;
import judgels.problem.partner.ProblemPartnerStore;
import judgels.user.Actor;
import judgels.user.role.ProblemAdminRoleChecker;

public class ProblemRoleChecker {
    private final ProblemAdminRoleChecker roleChecker;
    private final ProblemStore problemStore;
    private final ProblemPartnerStore partnerStore;

    @Inject
    public ProblemRoleChecker(ProblemAdminRoleChecker roleChecker, ProblemStore problemStore, ProblemPartnerStore partnerStore) {
        this.roleChecker = roleChecker;
        this.problemStore = problemStore;
        this.partnerStore = partnerStore;
    }

    public boolean isAdmin(String actorJid) {
        return roleChecker.isAdmin(actorJid);
    }

    public boolean isAdmin(Actor actor) {
        return roleChecker.isAdmin(actor);
    }

    public boolean isWriter(Actor actor) {
        return roleChecker.isWriter(actor);
    }

    public boolean canView(String actorJid, Problem problem) {
        return isAuthorOrAbove(actorJid, problem)
                || isPartner(actorJid, problem);
    }

    public boolean canView(Actor actor, Problem problem) {
        return canView(actor.getUserJid(), problem);
    }

    public boolean canEdit(String actorJid, Problem problem) {
        return isAuthorOrAbove(actorJid, problem)
                || isPartnerWithUpdatePermission(actorJid, problem);
    }

    public boolean canEdit(Actor actor, Problem problem) {
        return canEdit(actor.getUserJid(), problem);
    }

    public Optional<String> canSubmit(String actorJid, Problem problem) {
        if (!canEdit(actorJid, problem)) {
            return Optional.of("Submission not allowed.");
        }
        if (problemStore.userCloneExists(actorJid, problem.getJid())) {
            return Optional.of("Submission not allowed if there are local changes.");
        }
        return Optional.empty();
    }

    public Optional<String> canSubmit(Actor actor, Problem problem) {
        return canSubmit(actor.getUserJid(), problem);
    }

    public boolean isAuthor(String actorJid, Problem problem) {
        return problem.getAuthorJid().equals(actorJid);
    }

    public boolean isAuthor(Actor actor, Problem problem) {
        return isAuthor(actor.getUserJid(), problem);
    }

    public boolean isAuthorOrAbove(String actorJid, Problem problem) {
        return isAdmin(actorJid) || isAuthor(actorJid, problem);
    }

    public boolean isAuthorOrAbove(Actor actor, Problem problem) {
        return isAuthorOrAbove(actor.getUserJid(), problem);
    }

    private boolean isPartner(String actorJid, Problem problem) {
        Optional<Partner> partner = partnerStore.getPartner(problem.getJid(), actorJid);
        return partner.isPresent();
    }

    private boolean isPartnerWithUpdatePermission(String actorJid, Problem problem) {
        Optional<Partner> partner = partnerStore.getPartner(problem.getJid(), actorJid);
        return partner.isPresent() && partner.get().getPermission() == PartnerPermission.UPDATE;
    }
}
