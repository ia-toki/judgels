package judgels.lesson;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.api.actor.Actor;
import judgels.api.lesson.Lesson;
import judgels.api.resource.Partner;
import judgels.api.resource.PartnerPermission;
import judgels.lesson.partner.LessonPartnerStore;
import judgels.role.ProblemAdminRoleChecker;

public class LessonRoleChecker {
    private final ProblemAdminRoleChecker roleChecker;
    private final LessonPartnerStore partnerStore;

    @Inject
    public LessonRoleChecker(ProblemAdminRoleChecker roleChecker, LessonPartnerStore partnerStore) {
        this.roleChecker = roleChecker;
        this.partnerStore = partnerStore;
    }

    public boolean isAdmin(Actor actor) {
        return roleChecker.isAdmin(actor);
    }

    public boolean isWriter(Actor actor) {
        return roleChecker.isWriter(actor);
    }

    public boolean canView(Actor actor, Lesson lesson) {
        return isAuthorOrAbove(actor, lesson)
                || isPartner(actor, lesson);
    }

    public boolean canEdit(Actor actor, Lesson lesson) {
        return isAuthorOrAbove(actor, lesson)
                || isPartnerWithUpdatePermission(actor, lesson);
    }

    public boolean isAuthor(Actor actor, Lesson lesson) {
        return lesson.getAuthorJid().equals(actor.getUserJid());
    }

    private boolean isAuthorOrAbove(Actor actor, Lesson lesson) {
        return roleChecker.isAdmin(actor) || isAuthor(actor, lesson);
    }

    private boolean isPartner(Actor actor, Lesson lesson) {
        Optional<Partner> partner = partnerStore.getPartner(lesson.getJid(), actor.getUserJid());
        return partner.isPresent();
    }

    private boolean isPartnerWithUpdatePermission(Actor actor, Lesson lesson) {
        Optional<Partner> partner = partnerStore.getPartner(lesson.getJid(), actor.getUserJid());
        return partner.isPresent() && partner.get().getPermission() == PartnerPermission.UPDATE;
    }
}
