package judgels.uriel.api.contest.file;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static judgels.uriel.api.contest.module.ContestModuleType.FILE;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestFileServicePermissionIntegrationTests extends AbstractContestServiceIntegrationTests {
    private final ContestFileService fileService = createService(ContestFileService.class);
    private final WebTarget webTarget = createWebTarget();

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
        assert403(uploadFile(ADMIN_BEARER_TOKEN));
        assert403(uploadFile(MANAGER_BEARER_TOKEN));
        assert403(uploadFile(SUPERVISOR_A_BEARER_TOKEN));
        assert403(uploadFile(SUPERVISOR_B_BEARER_TOKEN));
        assert403(uploadFile(CONTESTANT_BEARER_TOKEN));
        assert403(uploadFile(USER_BEARER_TOKEN));

        enableModule(contest, FILE);

        assert204(uploadFile(ADMIN_BEARER_TOKEN));
        assert204(uploadFile(MANAGER_BEARER_TOKEN));
        assert204(uploadFile(SUPERVISOR_A_BEARER_TOKEN));
        assert403(uploadFile(SUPERVISOR_B_BEARER_TOKEN));
        assert403(uploadFile(CONTESTANT_BEARER_TOKEN));
        assert403(uploadFile(USER_BEARER_TOKEN));
    }

    @Test
    void get_files() {
        assertForbidden(getFiles(ADMIN_HEADER));
        assertForbidden(getFiles(MANAGER_HEADER));
        assertForbidden(getFiles(SUPERVISOR_A_HEADER));
        assertForbidden(getFiles(SUPERVISOR_B_HEADER));
        assertForbidden(getFiles(CONTESTANT_HEADER));
        assertForbidden(getFiles(USER_HEADER));

        enableModule(contest, FILE);

        assertPermitted(getFiles(ADMIN_HEADER));
        assertPermitted(getFiles(MANAGER_HEADER));
        assertPermitted(getFiles(SUPERVISOR_A_HEADER));
        assertPermitted(getFiles(SUPERVISOR_B_HEADER));
        assertForbidden(getFiles(CONTESTANT_HEADER));
        assertForbidden(getFiles(USER_HEADER));
    }

    private int uploadFile(String token) {
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MULTIPART_FORM_DATA_TYPE);
        multiPart.bodyPart(new FormDataBodyPart(
                FormDataContentDisposition.name("file").fileName("filename").build(),
                new byte[]{},
                APPLICATION_OCTET_STREAM_TYPE));

        return webTarget
                .path("/api/v2/contests/" + contest.getJid() + "/files")
                .request()
                .header(AUTHORIZATION, "Bearer " + token)
                .post(Entity.entity(multiPart, multiPart.getMediaType()))
                .getStatus();
    }

    private ThrowingCallable getFiles(AuthHeader authHeader) {
        return () -> fileService.getFiles(authHeader, contest.getJid());
    }

    private void assert204(int status) {
        assertThat(status).isEqualTo(204);
    }

    private void assert403(int status) {
        assertThat(status).isEqualTo(403);
    }
}
