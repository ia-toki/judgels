package judgels.uriel.api.role;

import static java.util.Optional.empty;
import static judgels.uriel.api.mocks.MockJophiel.SUPERADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_B;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_JID;
import static judgels.uriel.api.mocks.MockJophiel.mockJophiel;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableSet;
import judgels.uriel.api.AbstractServiceIntegrationTests;
import judgels.uriel.api.admin.Admin;
import judgels.uriel.api.admin.AdminDeleteResponse;
import judgels.uriel.api.admin.AdminService;
import judgels.uriel.api.admin.AdminUpsertResponse;
import judgels.uriel.api.admin.AdminsResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AdminServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private static WireMockServer mockJophiel;

    private AdminService adminService = createService(AdminService.class);

    @BeforeAll
    static void setUpMocks() {
        mockJophiel = mockJophiel();
        mockJophiel.start();
    }

    @AfterAll
    static void tearDownMocks() {
        mockJophiel.shutdown();
    }

    @Test
    void end_to_end_flow() {
        AdminUpsertResponse response = adminService.upsertAdmins(SUPERADMIN_HEADER, ImmutableSet.of(USER_A));
        assertThat(response.getInsertedAdminProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(response.getAlreadyAdminProfilesMap()).isEmpty();

        response = adminService.upsertAdmins(SUPERADMIN_HEADER, ImmutableSet.of(USER_A, USER_B, "userC"));

        assertThat(response.getInsertedAdminProfilesMap()).containsOnlyKeys(USER_B);
        assertThat(response.getInsertedAdminProfilesMap().get(USER_B).getUsername()).isEqualTo(USER_B);
        assertThat(response.getAlreadyAdminProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(response.getAlreadyAdminProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);

        AdminsResponse allResponse = adminService.getAdmins(SUPERADMIN_HEADER, empty());
        assertThat(allResponse.getData().getPage()).containsOnly(
                new Admin.Builder().userJid(USER_A_JID).build(),
                new Admin.Builder().userJid(USER_B_JID).build());
        assertThat(allResponse.getProfilesMap().get(USER_A_JID).getUsername()).isEqualTo(USER_A);

        AdminDeleteResponse deleteResponse =
                adminService.deleteAdmins(SUPERADMIN_HEADER, ImmutableSet.of(USER_A, "userC"));
        assertThat(deleteResponse.getDeletedAdminProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(deleteResponse.getDeletedAdminProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);

        allResponse = adminService.getAdmins(SUPERADMIN_HEADER, empty());
        assertThat(allResponse.getData().getPage()).containsOnly(
                new Admin.Builder().userJid(USER_B_JID).build());
    }
}
