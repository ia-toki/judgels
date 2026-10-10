package judgels.catalog.lesson;

import static judgels.core.JudgelsRequestChecks.checkAllowed;
import static judgels.core.JudgelsRequestChecks.checkFound;

import jakarta.inject.Inject;
import judgels.api.catalog.lesson.Lesson;
import judgels.core.api.AuthHeader;
import judgels.session.ActorChecker;

/**
 * Guards the endpoints that read or write a lesson's files.
 *
 * A lesson's files are edited in the actor's own clone, which is created on their first write.
 * Every endpoint that writes them must go through {@link #checkCanEdit}, so that none writes to the origin.
 */
public class LessonAccessChecker {
    private final ActorChecker actorChecker;
    private final LessonRoleChecker roleChecker;
    private final LessonStore lessonStore;

    @Inject
    public LessonAccessChecker(ActorChecker actorChecker, LessonRoleChecker roleChecker, LessonStore lessonStore) {
        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.lessonStore = lessonStore;
    }

    public String checkCanView(AuthHeader authHeader, String lessonJid) {
        String actorJid = actorChecker.check(authHeader);
        Lesson lesson = checkFound(lessonStore.getLessonByJid(lessonJid));
        checkAllowed(roleChecker.canView(actorJid, lesson));
        return actorJid;
    }

    public String checkCanEdit(AuthHeader authHeader, String lessonJid) {
        String actorJid = actorChecker.check(authHeader);
        Lesson lesson = checkFound(lessonStore.getLessonByJid(lessonJid));
        checkAllowed(roleChecker.canEdit(actorJid, lesson));

        lessonStore.createUserCloneIfNotExists(actorJid, lessonJid);
        return actorJid;
    }
}
