package judgels.catalog.lesson;

import jakarta.inject.Inject;
import judgels.api.catalog.lesson.Lesson;
import judgels.catalog.lesson.partner.LessonPartnerStore;
import judgels.session.Actor;
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

    // Every partner can edit the lesson, so whoever can view it can edit it too.
    public boolean canEdit(String actorJid, Lesson lesson) {
        return canView(actorJid, lesson);
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
        return partnerStore.isPartner(lesson.getJid(), actorJid);
    }
}
