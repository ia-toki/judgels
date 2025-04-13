package judgels.uriel.api;

import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.uriel.api.contest.module.ContestModuleType.FILE;

import feign.form.FormData;
import judgels.uriel.ContestFileClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestFileApiPermissionIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestFileClient fileClient = createClient(ContestFileClient.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .supervisorWithManagementPermissions(SUPERVISOR_A, SupervisorManagementPermission.FILE)
                .supervisors(SUPERVISOR_B)
                .build();
    }

    @Test
    void upload_file() {
        assertForbidden(uploadFile(adminToken));
        assertForbidden(uploadFile(managerToken));
        assertForbidden(uploadFile(supervisorAToken));
        assertForbidden(uploadFile(supervisorBToken));
        assertForbidden(uploadFile(contestantToken));
        assertForbidden(uploadFile(userToken));

        enableModule(contest, FILE);

        assertPermitted(uploadFile(adminToken));
        assertPermitted(uploadFile(managerToken));
        assertPermitted(uploadFile(supervisorAToken));
        assertForbidden(uploadFile(supervisorBToken));
        assertForbidden(uploadFile(contestantToken));
        assertForbidden(uploadFile(userToken));
    }

    @Test
    void get_files() {
        assertForbidden(getFiles(adminToken));
        assertForbidden(getFiles(managerToken));
        assertForbidden(getFiles(supervisorAToken));
        assertForbidden(getFiles(supervisorBToken));
        assertForbidden(getFiles(contestantToken));
        assertForbidden(getFiles(userToken));

        enableModule(contest, FILE);

        assertPermitted(getFiles(adminToken));
        assertPermitted(getFiles(managerToken));
        assertPermitted(getFiles(supervisorAToken));
        assertPermitted(getFiles(supervisorBToken));
        assertForbidden(getFiles(contestantToken));
        assertForbidden(getFiles(userToken));
    }

    private ThrowingCallable uploadFile(String token) {
        var formData = new FormData(MULTIPART_FORM_DATA, "filename", new byte[]{});
        return () -> fileClient.uploadFile(token, contest.getJid(), formData);
    }

    private ThrowingCallable getFiles(String token) {
        return () -> fileClient.getFiles(token, contest.getJid());
    }
}
