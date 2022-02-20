package judgels.uriel.api.contest.file;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static judgels.uriel.api.contest.module.ContestModuleType.FILE;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestFileServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private final ContestFileService fileService = createService(ContestFileService.class);
    private final WebTarget webTarget = createWebTarget();

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .supervisorWithManagementPermissions(SUPERVISOR_A, SupervisorManagementPermission.FILE)
                .supervisors(SUPERVISOR_B)
                .modules(FILE)
                .build();
    }

    @Test
    void upload_get_files() {
        uploadFile(MANAGER_BEARER_TOKEN, "editorial.txt", "EDITORIAL");
        uploadFile(MANAGER_BEARER_TOKEN, "hints.txt", "HINT");

        Map<AuthHeader, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(ADMIN_HEADER, true);
        canManageMap.put(MANAGER_HEADER, true);
        canManageMap.put(SUPERVISOR_A_HEADER, true);
        canManageMap.put(SUPERVISOR_B_HEADER, false);

        for (AuthHeader authHeader : canManageMap.keySet()) {
            ContestFilesResponse response = fileService.getFiles(authHeader, contest.getJid());

            List<ContestFile> files = response.getData();
            assertThat(files).extracting("name").containsOnly("editorial.txt", "hints.txt");

            ContestFileConfig config = response.getConfig();
            assertThat(config.getCanManage()).isEqualTo(canManageMap.get(authHeader));
        }
    }

    private Response uploadFile(String token, String filename, String content) {
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MULTIPART_FORM_DATA_TYPE);
        multiPart.bodyPart(new FormDataBodyPart(
                FormDataContentDisposition.name("file").fileName(filename).build(),
                content.getBytes(),
                APPLICATION_OCTET_STREAM_TYPE));

        return webTarget
                .path("/api/v2/contests/" + contest.getJid() + "/files")
                .request()
                .header(AUTHORIZATION, "Bearer " + token)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));
    }
}
