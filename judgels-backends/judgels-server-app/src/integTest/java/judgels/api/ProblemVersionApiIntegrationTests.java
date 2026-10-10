package judgels.api;

import static judgels.api.catalog.problem.ProblemErrors.VERSION_LOCAL_CHANGES_CONFLICT;
import static judgels.api.catalog.problem.ProblemErrors.VERSION_LOCAL_CHANGES_OUTDATED;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.CatalogVersion;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.ProblemStatement;
import judgels.api.catalog.problem.version.ProblemVersionCommitData;
import judgels.api.catalog.problem.version.ProblemVersionsResponse;
import judgels.client.ProblemClient;
import judgels.client.ProblemStatementClient;
import judgels.client.ProblemVersionClient;
import org.junit.jupiter.api.Test;

class ProblemVersionApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final ProblemClient problemClient = createClient(ProblemClient.class);
    private final ProblemStatementClient statementClient = createClient(ProblemStatementClient.class);
    private final ProblemVersionClient versionClient = createClient(ProblemVersionClient.class);

    @Test
    void commit_discard_local_changes() {
        Problem problem = createProblem(adminToken, "version-problem");
        String problemJid = problem.getJid();

        ProblemVersionsResponse response = versionClient.getVersions(adminToken, problemJid);
        assertThat(response.getData()).extracting(CatalogVersion::getTitle).containsExactly("Initial commit");
        assertThat(response.getData().get(0).getUserJid()).isEqualTo(admin.getJid());
        assertThat(response.getData().get(0).getDescription()).isEmpty();
        assertThat(response.getProfilesMap()).containsOnlyKeys(admin.getJid());

        // There is nothing to commit or rebase yet, and asking must not start any local changes.
        assertBadRequest(() -> versionClient.commitVersionLocalChanges(adminToken, problemJid, commitData("Empty")));
        assertBadRequest(() -> versionClient.rebaseVersionLocalChanges(adminToken, problemJid));
        assertPermitted(() -> versionClient.discardVersionLocalChanges(adminToken, problemJid));
        assertThat(problemClient.getProblem(adminToken, problemJid).getHasLocalChanges()).isFalse();

        ProblemStatement committedStatement = statementClient.getStatement(adminToken, problemJid, "en-US");

        statementClient.updateStatement(adminToken, problemJid, "en-US", statement("Discarded"));
        versionClient.discardVersionLocalChanges(adminToken, problemJid);

        assertThat(problemClient.getProblem(adminToken, problemJid).getHasLocalChanges()).isFalse();
        assertThat(statementClient.getStatement(adminToken, problemJid, "en-US")).isEqualTo(committedStatement);

        statementClient.updateStatement(adminToken, problemJid, "en-US", statement("Committed"));

        // Another editor keeps seeing the committed version until the changes are committed.
        assertThat(statementClient.getStatement(superadminToken, problemJid, "en-US")).isEqualTo(committedStatement);

        versionClient.commitVersionLocalChanges(adminToken, problemJid, new ProblemVersionCommitData.Builder()
                .title("Update statement")
                .description("Reworded the statement.")
                .build());

        assertThat(problemClient.getProblem(adminToken, problemJid).getHasLocalChanges()).isFalse();
        assertThat(statementClient.getStatement(superadminToken, problemJid, "en-US")).isEqualTo(statement("Committed"));

        List<CatalogVersion> versions = versionClient.getVersions(adminToken, problemJid).getData();
        assertThat(versions).extracting(CatalogVersion::getTitle).containsExactly("Update statement", "Initial commit");
        assertThat(versions.get(0).getDescription()).isEqualTo("Reworded the statement.");
        assertThat(versions.get(0).getUserJid()).isEqualTo(admin.getJid());

        assertForbidden(() -> versionClient.getVersions(userToken, problemJid));
        assertForbidden(() -> versionClient.commitVersionLocalChanges(userToken, problemJid, commitData("Commit")));
        assertForbidden(() -> versionClient.discardVersionLocalChanges(userToken, problemJid));
    }

    @Test
    void rebase_local_changes() {
        Problem problem = createProblem(adminToken, "version-rebase-problem");
        String problemJid = problem.getJid();

        // The admin adds a language while the superadmin commits a new statement: the two do not conflict.
        statementClient.addStatementLanguage(adminToken, problemJid, "id-ID");

        statementClient.updateStatement(superadminToken, problemJid, "en-US", statement("By superadmin"));
        versionClient.commitVersionLocalChanges(superadminToken, problemJid, commitData("Update statement"));

        assertBadRequest(() -> versionClient.commitVersionLocalChanges(adminToken, problemJid, commitData("Add language")))
                .hasMessageContaining(VERSION_LOCAL_CHANGES_OUTDATED);

        versionClient.rebaseVersionLocalChanges(adminToken, problemJid);

        assertThat(problemClient.getProblem(adminToken, problemJid).getHasLocalChanges()).isTrue();
        assertThat(statementClient.getStatement(adminToken, problemJid, "en-US")).isEqualTo(statement("By superadmin"));

        versionClient.commitVersionLocalChanges(adminToken, problemJid, commitData("Add language"));

        assertThat(versionClient.getVersions(adminToken, problemJid).getData())
                .extracting(CatalogVersion::getTitle)
                .containsExactly("Add language", "Update statement", "Initial commit");
        assertThat(statementClient.getStatementLanguages(superadminToken, problemJid).getEnabledLanguages())
                .containsExactlyInAnyOrder("en-US", "id-ID");

        // Now both write the same statement, so the admin's changes cannot be rebased.
        statementClient.updateStatement(adminToken, problemJid, "en-US", statement("By admin"));

        statementClient.updateStatement(superadminToken, problemJid, "en-US", statement("By superadmin again"));
        versionClient.commitVersionLocalChanges(superadminToken, problemJid, commitData("Update statement again"));

        assertBadRequest(() -> versionClient.rebaseVersionLocalChanges(adminToken, problemJid))
                .hasMessageContaining(VERSION_LOCAL_CHANGES_CONFLICT);

        // The failed rebase leaves the local changes as they were.
        assertThat(statementClient.getStatement(adminToken, problemJid, "en-US")).isEqualTo(statement("By admin"));

        assertForbidden(() -> versionClient.rebaseVersionLocalChanges(userToken, problemJid));
    }

    @Test
    void restore_version() {
        Problem problem = createProblem(adminToken, "version-restore-problem");
        String problemJid = problem.getJid();

        statementClient.updateStatement(adminToken, problemJid, "en-US", statement("First"));
        versionClient.commitVersionLocalChanges(adminToken, problemJid, commitData("First"));

        statementClient.updateStatement(adminToken, problemJid, "en-US", statement("Second"));
        versionClient.commitVersionLocalChanges(adminToken, problemJid, commitData("Second"));

        List<CatalogVersion> versions = versionClient.getVersions(adminToken, problemJid).getData();
        String latestHash = versions.get(0).getHash();
        String firstHash = versions.get(1).getHash();

        assertNotFound(() -> versionClient.restoreVersion(adminToken, problemJid, "bogus"));
        assertBadRequest(() -> versionClient.restoreVersion(adminToken, problemJid, latestHash));
        assertForbidden(() -> versionClient.restoreVersion(userToken, problemJid, firstHash));

        // A restore replaces the version that local changes were made on.
        statementClient.addStatementLanguage(adminToken, problemJid, "id-ID");
        assertBadRequest(() -> versionClient.restoreVersion(adminToken, problemJid, firstHash));
        versionClient.discardVersionLocalChanges(adminToken, problemJid);

        versionClient.restoreVersion(adminToken, problemJid, firstHash);

        assertThat(statementClient.getStatement(adminToken, problemJid, "en-US")).isEqualTo(statement("First"));
        assertThat(versionClient.getVersions(adminToken, problemJid).getData())
                .extracting(CatalogVersion::getTitle)
                .containsExactly("Revert to commit " + firstHash.substring(0, 7), "Second", "First", "Initial commit");
    }

    private static ProblemStatement statement(String title) {
        return new ProblemStatement.Builder()
                .title(title)
                .text("<p>" + title + "</p>")
                .build();
    }

    private static ProblemVersionCommitData commitData(String title) {
        return new ProblemVersionCommitData.Builder()
                .title(title)
                .description("")
                .build();
    }
}
