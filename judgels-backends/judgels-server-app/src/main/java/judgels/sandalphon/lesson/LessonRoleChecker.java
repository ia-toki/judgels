package judgels.sandalphon.lesson;

import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.actor.Actor;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.resource.Partner;
import judgels.sandalphon.api.resource.PartnerPermission;
import judgels.sandalphon.lesson.partner.LessonPartnerStore;
import judgels.sandalphon.role.RoleChecker;

public class LessonRoleChecker {
    private final RoleChecker roleChecker;
    private final LessonPartnerStore partnerStore;

    @Inject
    public LessonRoleChecker(RoleChecker roleChecker, LessonPartnerStore partnerStore) {
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
