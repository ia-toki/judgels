package judgels.uriel.api.contest.file;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModuleService;
import judgels.uriel.api.contest.module.ContestModuleType;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.junit.jupiter.api.Test;

class ContestFileServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestFileService fileService = createService(ContestFileService.class);
    private ContestModuleService moduleService = createService(ContestModuleService.class);
    private WebTarget webTarget = createWebTarget();

    @Test
    void end_to_end_flow() {
        Contest contest = createContest("contest");
        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), ContestModuleType.FILE);

        uploadFile(contest, "editorial.txt", "EDITORIAL");
        uploadFile(contest, "hints.txt", "HINT");

        ContestFilesResponse response = fileService.getFiles(ADMIN_HEADER, contest.getJid());
        List<ContestFile> files = response.getData();

        assertThat(files).extracting("name").containsOnly("editorial.txt", "hints.txt");
    }

    private void uploadFile(Contest contest, String filename, String content) {
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MULTIPART_FORM_DATA_TYPE);
        multiPart.bodyPart(new FormDataBodyPart(
                FormDataContentDisposition.name("file").fileName(filename).build(),
                content.getBytes(),
                APPLICATION_OCTET_STREAM_TYPE));

        webTarget
                .path("/api/v2/contests/" + contest.getJid() + "/files")
                .request()
                .header(AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));
    }
}
