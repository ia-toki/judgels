package judgels.lesson;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.api.catalog.Partner;
import judgels.api.catalog.PartnerPermission;
import judgels.api.lesson.Lesson;
import judgels.lesson.partner.LessonPartnerStore;
import judgels.user.Actor;
import judgels.user.role.TrainingAdminRoleChecker;

public class LessonRoleChecker {
    private final TrainingAdminRoleChecker roleChecker;
    private final LessonPartnerStore partnerStore;

    @Inject
    public LessonRoleChecker(TrainingAdminRoleChecker roleChecker, LessonPartnerStore partnerStore) {
        this.roleChecker = roleChecker;
        this.partnerStore = partnerStore;
    }

    public boolean isAdmin(String actorJid) {
        return roleChecker.isAdmin(actorJid);
    }

    public boolean isAdmin(Actor actor) {
        return roleChecker.isAdmin(actor);
    }

    public boolean isWriter(Actor actor) {
        return true; // TODO(fushar): create separate role if necessary
    }

    public boolean canView(String actorJid, Lesson lesson) {
        return isAuthorOrAbove(actorJid, lesson)
                || isPartner(actorJid, lesson);
    }

    public boolean canView(Actor actor, Lesson lesson) {
        return canView(actor.getUserJid(), lesson);
    }

    public boolean canEdit(String actorJid, Lesson lesson) {
        return isAuthorOrAbove(actorJid, lesson)
                || isPartnerWithUpdatePermission(actorJid, lesson);
    }

    public boolean canEdit(Actor actor, Lesson lesson) {
        return canEdit(actor.getUserJid(), lesson);
    }

    public boolean isAuthor(String actorJid, Lesson lesson) {
        return lesson.getAuthorJid().equals(actorJid);
    }

    public boolean isAuthor(Actor actor, Lesson lesson) {
        return isAuthor(actor.getUserJid(), lesson);
    }

    public boolean isAuthorOrAbove(String actorJid, Lesson lesson) {
        return isAdmin(actorJid) || isAuthor(actorJid, lesson);
    }

    private boolean isPartner(String actorJid, Lesson lesson) {
        Optional<Partner> partner = partnerStore.getPartner(lesson.getJid(), actorJid);
        return partner.isPresent();
    }

    private boolean isPartnerWithUpdatePermission(String actorJid, Lesson lesson) {
        Optional<Partner> partner = partnerStore.getPartner(lesson.getJid(), actorJid);
        return partner.isPresent() && partner.get().getPermission() == PartnerPermission.UPDATE;
    }
}
