package judgels.uriel.api.role;

import static com.palantir.conjure.java.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static java.util.Optional.empty;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_B;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_JID;
import static judgels.uriel.api.mocks.MockJophiel.mockJophiel;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableSet;
import com.palantir.conjure.java.api.errors.ErrorType;
import java.util.Optional;
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
        // as superadmin

        AdminUpsertResponse upsertResponse = adminService.upsertAdmins(SUPERADMIN_HEADER, ImmutableSet.of(USER_A));
        assertThat(upsertResponse.getInsertedAdminProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(upsertResponse.getAlreadyAdminProfilesMap()).isEmpty();

        upsertResponse = adminService.upsertAdmins(SUPERADMIN_HEADER, ImmutableSet.of(USER_A, USER_B, "userC"));

        assertThat(upsertResponse.getInsertedAdminProfilesMap()).containsOnlyKeys(USER_B);
        assertThat(upsertResponse.getInsertedAdminProfilesMap().get(USER_B).getUsername()).isEqualTo(USER_B);
        assertThat(upsertResponse.getAlreadyAdminProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(upsertResponse.getAlreadyAdminProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);

        AdminsResponse response = adminService.getAdmins(SUPERADMIN_HEADER, empty());
        assertThat(response.getData().getPage()).containsOnly(
                new Admin.Builder().userJid(USER_A_JID).build(),
                new Admin.Builder().userJid(USER_B_JID).build());
        assertThat(response.getProfilesMap().get(USER_A_JID).getUsername()).isEqualTo(USER_A);

        AdminDeleteResponse deleteResponse =
                adminService.deleteAdmins(SUPERADMIN_HEADER, ImmutableSet.of(USER_A, "userC"));
        assertThat(deleteResponse.getDeletedAdminProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(deleteResponse.getDeletedAdminProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);

        response = adminService.getAdmins(SUPERADMIN_HEADER, empty());
        assertThat(response.getData().getPage()).containsOnly(
                new Admin.Builder().userJid(USER_B_JID).build());

        // as admin

        assertThatRemoteExceptionThrownBy(() -> adminService.getAdmins(ADMIN_HEADER, Optional.empty()))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        assertThatRemoteExceptionThrownBy(() -> adminService.upsertAdmins(ADMIN_HEADER, ImmutableSet.of(USER_A)))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        assertThatRemoteExceptionThrownBy(() -> adminService.deleteAdmins(ADMIN_HEADER, ImmutableSet.of(USER_A)))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);
    }
}
