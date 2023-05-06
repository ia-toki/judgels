package judgels.uriel.api.contest.file;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static judgels.uriel.api.contest.module.ContestModuleType.FILE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestFileServicePermissionIntegrationTests extends BaseUrielServiceIntegrationTests {
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
        assert403(uploadFile(adminHeader.getBearerToken()));
        assert403(uploadFile(managerHeader.getBearerToken()));
        assert403(uploadFile(supervisorAHeader.getBearerToken()));
        assert403(uploadFile(supervisorBHeader.getBearerToken()));
        assert403(uploadFile(contestantHeader.getBearerToken()));
        assert403(uploadFile(userHeader.getBearerToken()));

        enableModule(contest, FILE);

        assert204(uploadFile(adminHeader.getBearerToken()));
        assert204(uploadFile(managerHeader.getBearerToken()));
        assert204(uploadFile(supervisorAHeader.getBearerToken()));
        assert403(uploadFile(supervisorBHeader.getBearerToken()));
        assert403(uploadFile(contestantHeader.getBearerToken()));
        assert403(uploadFile(userHeader.getBearerToken()));
    }

    @Test
    void get_files() {
        assertForbidden(getFiles(adminHeader));
        assertForbidden(getFiles(managerHeader));
        assertForbidden(getFiles(supervisorAHeader));
        assertForbidden(getFiles(supervisorBHeader));
        assertForbidden(getFiles(contestantHeader));
        assertForbidden(getFiles(userHeader));

        enableModule(contest, FILE);

        assertPermitted(getFiles(adminHeader));
        assertPermitted(getFiles(managerHeader));
        assertPermitted(getFiles(supervisorAHeader));
        assertPermitted(getFiles(supervisorBHeader));
        assertForbidden(getFiles(contestantHeader));
        assertForbidden(getFiles(userHeader));
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
