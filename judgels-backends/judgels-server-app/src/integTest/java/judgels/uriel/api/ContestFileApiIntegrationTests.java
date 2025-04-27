package judgels.uriel.api;

import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.uriel.api.contest.module.ContestModuleType.FILE;
import static org.assertj.core.api.Assertions.assertThat;

import feign.form.FormData;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import judgels.uriel.ContestFileClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.file.ContestFile;
import judgels.uriel.api.contest.file.ContestFileConfig;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestFileApiIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestFileClient fileClient = createClient(ContestFileClient.class);

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
        uploadFile(managerToken, "editorial.txt", "EDITORIAL");
        uploadFile(managerToken, "hints.txt", "HINT");

        Map<String, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(adminToken, true);
        canManageMap.put(managerToken, true);
        canManageMap.put(supervisorAToken, true);
        canManageMap.put(supervisorBToken, false);

        for (String token : canManageMap.keySet()) {
            var response = fileClient.getFiles(token, contest.getJid());

            List<ContestFile> files = response.getData();
            assertThat(files).extracting("name").containsOnly("editorial.txt", "hints.txt");

            ContestFileConfig config = response.getConfig();
            assertThat(config.getCanManage()).isEqualTo(canManageMap.get(token));
        }
    }

    private void uploadFile(String token, String filename, String content) {
        var formData = new FormData(MULTIPART_FORM_DATA, filename, content.getBytes());
        fileClient.uploadFile(token, contest.getJid(), formData);
    }
}
