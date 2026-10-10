package judgels.catalog.problem.version;

import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import judgels.api.catalog.problem.ProblemErrors;
import judgels.catalog.problem.ProblemStore;
import judgels.catalog.problem.tag.ProblemTagStore;
import judgels.core.git.GitCommit;

/**
 * Moves a problem's files between the actor's clone, which holds their local changes, and the origin,
 * which holds the committed versions that contests and training use.
 */
public class ProblemVersionService {
    private final ProblemStore problemStore;
    private final ProblemVersionStore versionStore;
    private final ProblemTagStore tagStore;

    @Inject
    public ProblemVersionService(
            ProblemStore problemStore,
            ProblemVersionStore versionStore,
            ProblemTagStore tagStore) {

        this.problemStore = problemStore;
        this.versionStore = versionStore;
        this.tagStore = tagStore;
    }

    public void commitLocalChanges(String actorJid, String problemJid, String title, String description) {
        checkHasLocalChanges(actorJid, problemJid);

        if (versionStore.fetchUserClone(actorJid, problemJid)) {
            throw ProblemErrors.versionLocalChangesOutdated();
        }
        if (!versionStore.commitThenMergeUserClone(actorJid, problemJid, title, description)
                || !versionStore.pushUserClone(actorJid, problemJid)) {
            throw ProblemErrors.versionLocalChangesConflict();
        }

        versionStore.discardUserClone(actorJid, problemJid);
        tagStore.refreshDerivedTags(problemJid);
    }

    public void rebaseLocalChanges(String actorJid, String problemJid) {
        checkHasLocalChanges(actorJid, problemJid);

        versionStore.fetchUserClone(actorJid, problemJid);
        if (!versionStore.updateUserClone(actorJid, problemJid)) {
            throw ProblemErrors.versionLocalChangesConflict();
        }
    }

    public void discardLocalChanges(String actorJid, String problemJid) {
        if (problemStore.userCloneExists(actorJid, problemJid)) {
            versionStore.discardUserClone(actorJid, problemJid);
        }
    }

    public void restoreVersion(String actorJid, String problemJid, String versionHash) {
        // Local changes were made on top of the latest version, which a restore replaces.
        if (problemStore.userCloneExists(actorJid, problemJid)) {
            throw new BadRequestException();
        }

        List<String> hashes = Lists.transform(versionStore.getVersions(null, problemJid), GitCommit::getHash);
        if (!hashes.contains(versionHash)) {
            throw new NotFoundException();
        }
        // The latest version is already the current one.
        if (hashes.indexOf(versionHash) == 0) {
            throw new BadRequestException();
        }

        versionStore.restore(problemJid, versionHash);
        tagStore.refreshDerivedTags(problemJid);
    }

    private void checkHasLocalChanges(String actorJid, String problemJid) {
        if (!problemStore.userCloneExists(actorJid, problemJid)) {
            throw new BadRequestException();
        }
    }
}
