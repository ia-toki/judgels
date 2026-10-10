package judgels.api;

import static judgels.api.catalog.lesson.LessonErrors.VERSION_LOCAL_CHANGES_CONFLICT;
import static judgels.api.catalog.lesson.LessonErrors.VERSION_LOCAL_CHANGES_OUTDATED;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.CatalogVersion;
import judgels.api.catalog.lesson.Lesson;
import judgels.api.catalog.lesson.LessonStatement;
import judgels.api.catalog.lesson.version.LessonVersionCommitData;
import judgels.api.catalog.lesson.version.LessonVersionsResponse;
import judgels.client.LessonClient;
import judgels.client.LessonStatementClient;
import judgels.client.LessonVersionClient;
import org.junit.jupiter.api.Test;

class LessonVersionApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final LessonClient lessonClient = createClient(LessonClient.class);
    private final LessonStatementClient statementClient = createClient(LessonStatementClient.class);
    private final LessonVersionClient versionClient = createClient(LessonVersionClient.class);

    @Test
    void commit_discard_local_changes() {
        Lesson lesson = createLesson(adminToken, "version-lesson");
        String lessonJid = lesson.getJid();

        LessonVersionsResponse response = versionClient.getVersions(adminToken, lessonJid);
        assertThat(response.getData()).extracting(CatalogVersion::getTitle).containsExactly("Initial commit");
        assertThat(response.getData().get(0).getUserJid()).isEqualTo(admin.getJid());
        assertThat(response.getData().get(0).getDescription()).isEmpty();
        assertThat(response.getProfilesMap()).containsOnlyKeys(admin.getJid());

        // There is nothing to commit or rebase yet, and asking must not start any local changes.
        assertBadRequest(() -> versionClient.commitVersionLocalChanges(adminToken, lessonJid, commitData("Empty")));
        assertBadRequest(() -> versionClient.rebaseVersionLocalChanges(adminToken, lessonJid));
        assertPermitted(() -> versionClient.discardVersionLocalChanges(adminToken, lessonJid));
        assertThat(lessonClient.getLesson(adminToken, lessonJid).getHasLocalChanges()).isFalse();

        LessonStatement committedStatement = statementClient.getStatement(adminToken, lessonJid, "en-US");

        statementClient.updateStatement(adminToken, lessonJid, "en-US", statement("Discarded"));
        versionClient.discardVersionLocalChanges(adminToken, lessonJid);

        assertThat(lessonClient.getLesson(adminToken, lessonJid).getHasLocalChanges()).isFalse();
        assertThat(statementClient.getStatement(adminToken, lessonJid, "en-US")).isEqualTo(committedStatement);

        statementClient.updateStatement(adminToken, lessonJid, "en-US", statement("Committed"));

        // Another editor keeps seeing the committed version until the changes are committed.
        assertThat(statementClient.getStatement(superadminToken, lessonJid, "en-US")).isEqualTo(committedStatement);

        versionClient.commitVersionLocalChanges(adminToken, lessonJid, new LessonVersionCommitData.Builder()
                .title("Update statement")
                .description("Reworded the statement.")
                .build());

        assertThat(lessonClient.getLesson(adminToken, lessonJid).getHasLocalChanges()).isFalse();
        assertThat(statementClient.getStatement(superadminToken, lessonJid, "en-US")).isEqualTo(statement("Committed"));

        List<CatalogVersion> versions = versionClient.getVersions(adminToken, lessonJid).getData();
        assertThat(versions).extracting(CatalogVersion::getTitle).containsExactly("Update statement", "Initial commit");
        assertThat(versions.get(0).getDescription()).isEqualTo("Reworded the statement.");
        assertThat(versions.get(0).getUserJid()).isEqualTo(admin.getJid());

        assertForbidden(() -> versionClient.getVersions(userToken, lessonJid));
        assertForbidden(() -> versionClient.commitVersionLocalChanges(userToken, lessonJid, commitData("Commit")));
        assertForbidden(() -> versionClient.discardVersionLocalChanges(userToken, lessonJid));
    }

    @Test
    void rebase_local_changes() {
        Lesson lesson = createLesson(adminToken, "version-rebase-lesson");
        String lessonJid = lesson.getJid();

        // The admin adds a language while the superadmin commits a new statement: the two do not conflict.
        statementClient.addStatementLanguage(adminToken, lessonJid, "id-ID");

        statementClient.updateStatement(superadminToken, lessonJid, "en-US", statement("By superadmin"));
        versionClient.commitVersionLocalChanges(superadminToken, lessonJid, commitData("Update statement"));

        assertBadRequest(() -> versionClient.commitVersionLocalChanges(adminToken, lessonJid, commitData("Add language")))
                .hasMessageContaining(VERSION_LOCAL_CHANGES_OUTDATED);

        versionClient.rebaseVersionLocalChanges(adminToken, lessonJid);

        assertThat(lessonClient.getLesson(adminToken, lessonJid).getHasLocalChanges()).isTrue();
        assertThat(statementClient.getStatement(adminToken, lessonJid, "en-US")).isEqualTo(statement("By superadmin"));

        versionClient.commitVersionLocalChanges(adminToken, lessonJid, commitData("Add language"));

        assertThat(versionClient.getVersions(adminToken, lessonJid).getData())
                .extracting(CatalogVersion::getTitle)
                .containsExactly("Add language", "Update statement", "Initial commit");
        assertThat(statementClient.getStatementLanguages(superadminToken, lessonJid).getEnabledLanguages())
                .containsExactlyInAnyOrder("en-US", "id-ID");

        // Now both write the same statement, so the admin's changes cannot be rebased.
        statementClient.updateStatement(adminToken, lessonJid, "en-US", statement("By admin"));

        statementClient.updateStatement(superadminToken, lessonJid, "en-US", statement("By superadmin again"));
        versionClient.commitVersionLocalChanges(superadminToken, lessonJid, commitData("Update statement again"));

        assertBadRequest(() -> versionClient.rebaseVersionLocalChanges(adminToken, lessonJid))
                .hasMessageContaining(VERSION_LOCAL_CHANGES_CONFLICT);

        // The failed rebase leaves the local changes as they were.
        assertThat(statementClient.getStatement(adminToken, lessonJid, "en-US")).isEqualTo(statement("By admin"));

        assertForbidden(() -> versionClient.rebaseVersionLocalChanges(userToken, lessonJid));
    }

    @Test
    void restore_version() {
        Lesson lesson = createLesson(adminToken, "version-restore-lesson");
        String lessonJid = lesson.getJid();

        statementClient.updateStatement(adminToken, lessonJid, "en-US", statement("First"));
        versionClient.commitVersionLocalChanges(adminToken, lessonJid, commitData("First"));

        statementClient.updateStatement(adminToken, lessonJid, "en-US", statement("Second"));
        versionClient.commitVersionLocalChanges(adminToken, lessonJid, commitData("Second"));

        List<CatalogVersion> versions = versionClient.getVersions(adminToken, lessonJid).getData();
        String latestHash = versions.get(0).getHash();
        String firstHash = versions.get(1).getHash();

        assertNotFound(() -> versionClient.restoreVersion(adminToken, lessonJid, "bogus"));
        assertBadRequest(() -> versionClient.restoreVersion(adminToken, lessonJid, latestHash));
        assertForbidden(() -> versionClient.restoreVersion(userToken, lessonJid, firstHash));

        // A restore replaces the version that local changes were made on.
        statementClient.addStatementLanguage(adminToken, lessonJid, "id-ID");
        assertBadRequest(() -> versionClient.restoreVersion(adminToken, lessonJid, firstHash));
        versionClient.discardVersionLocalChanges(adminToken, lessonJid);

        versionClient.restoreVersion(adminToken, lessonJid, firstHash);

        assertThat(statementClient.getStatement(adminToken, lessonJid, "en-US")).isEqualTo(statement("First"));
        assertThat(versionClient.getVersions(adminToken, lessonJid).getData())
                .extracting(CatalogVersion::getTitle)
                .containsExactly("Revert to commit " + firstHash.substring(0, 7), "Second", "First", "Initial commit");
    }

    private static LessonStatement statement(String title) {
        return new LessonStatement.Builder()
                .title(title)
                .text("<p>" + title + "</p>")
                .build();
    }

    private static LessonVersionCommitData commitData(String title) {
        return new LessonVersionCommitData.Builder()
                .title(title)
                .description("")
                .build();
    }
}
