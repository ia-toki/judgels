package judgels.sandalphon.lesson;

import javax.inject.Inject;
import judgels.jophiel.api.actor.Actor;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.role.RoleChecker;

public class LessonRoleChecker {
    private final RoleChecker roleChecker;
    private final LessonStore lessonStore;

    @Inject
    public LessonRoleChecker(RoleChecker roleChecker, LessonStore lessonStore) {
        this.roleChecker = roleChecker;
        this.lessonStore = lessonStore;
    }

    public boolean canView(Actor actor, Lesson lesson) {
        return isPartnerOrAbove(actor, lesson);
    }

    public boolean canEdit(Actor actor, Lesson lesson) {
        return isAuthorOrAbove(actor, lesson);
    }

    private boolean isAuthor(Actor actor, Lesson lesson) {
        return lesson.getAuthorJid().equals(actor.getUserJid());
    }

    private boolean isAuthorOrAbove(Actor actor, Lesson lesson) {
        return roleChecker.isAdmin(actor) || isAuthor(actor, lesson);
    }

    private boolean isPartner(Actor actor, Lesson lesson) {
        return lessonStore.isUserPartnerForLesson(lesson.getJid(), actor.getUserJid());
    }

    private boolean isPartnerOrAbove(Actor actor, Lesson lesson) {
        return isAuthorOrAbove(actor, lesson) || isPartner(actor, lesson);
    }
}
