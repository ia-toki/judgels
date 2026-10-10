package judgels.catalog.lesson.version;

import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import judgels.api.catalog.lesson.LessonErrors;
import judgels.catalog.lesson.LessonStore;
import judgels.core.git.GitCommit;

/**
 * Moves a lesson's files between the actor's clone, which holds their local changes, and the origin,
 * which holds the committed versions that training uses.
 */
public class LessonVersionService {
    private final LessonStore lessonStore;
    private final LessonVersionStore versionStore;

    @Inject
    public LessonVersionService(LessonStore lessonStore, LessonVersionStore versionStore) {
        this.lessonStore = lessonStore;
        this.versionStore = versionStore;
    }

    public void commitLocalChanges(String actorJid, String lessonJid, String title, String description) {
        checkHasLocalChanges(actorJid, lessonJid);

        if (versionStore.fetchUserClone(actorJid, lessonJid)) {
            throw LessonErrors.versionLocalChangesOutdated();
        }
        if (!versionStore.commitThenMergeUserClone(actorJid, lessonJid, title, description)
                || !versionStore.pushUserClone(actorJid, lessonJid)) {
            throw LessonErrors.versionLocalChangesConflict();
        }

        versionStore.discardUserClone(actorJid, lessonJid);
    }

    public void rebaseLocalChanges(String actorJid, String lessonJid) {
        checkHasLocalChanges(actorJid, lessonJid);

        versionStore.fetchUserClone(actorJid, lessonJid);
        if (!versionStore.updateUserClone(actorJid, lessonJid)) {
            throw LessonErrors.versionLocalChangesConflict();
        }
    }

    public void discardLocalChanges(String actorJid, String lessonJid) {
        if (lessonStore.userCloneExists(actorJid, lessonJid)) {
            versionStore.discardUserClone(actorJid, lessonJid);
        }
    }

    public void restoreVersion(String actorJid, String lessonJid, String versionHash) {
        // Local changes were made on top of the latest version, which a restore replaces.
        if (lessonStore.userCloneExists(actorJid, lessonJid)) {
            throw new BadRequestException();
        }

        List<String> hashes = Lists.transform(versionStore.getVersions(null, lessonJid), GitCommit::getHash);
        if (!hashes.contains(versionHash)) {
            throw new NotFoundException();
        }
        // The latest version is already the current one.
        if (hashes.indexOf(versionHash) == 0) {
            throw new BadRequestException();
        }

        versionStore.restore(lessonJid, versionHash);
    }

    private void checkHasLocalChanges(String actorJid, String lessonJid) {
        if (!lessonStore.userCloneExists(actorJid, lessonJid)) {
            throw new BadRequestException();
        }
    }
}
