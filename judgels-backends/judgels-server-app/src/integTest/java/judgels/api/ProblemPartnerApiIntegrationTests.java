package judgels.api;

import static judgels.api.catalog.problem.ProblemErrors.PARTNER_USERNAMES_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.Partner;
import judgels.api.catalog.PartnerPermission;
import judgels.api.catalog.problem.ProblemPermissions;
import judgels.api.catalog.problem.partner.ProblemPartnerData;
import judgels.api.catalog.problem.partner.ProblemPartnersResponse;
import judgels.api.user.User;
import judgels.client.ProblemClient;
import judgels.client.ProblemPartnerClient;
import org.junit.jupiter.api.Test;

class ProblemPartnerApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final ProblemClient problemClient = createClient(ProblemClient.class);
    private final ProblemPartnerClient partnerClient = createClient(ProblemPartnerClient.class);

    @Test
    void set_partners() {
        User editor = createUser("editor");
        String editorToken = getToken(editor);
        User viewer = createUser("viewer");
        String viewerToken = getToken(viewer);

        String problemJid = createProblem(adminToken, "problem").getJid();

        // as admin

        ProblemPartnersResponse response = partnerClient.getPartners(adminToken, problemJid);
        assertThat(response.getData()).isEmpty();
        assertThat(response.getProfilesMap()).isEmpty();

        List<ProblemPartnerData> data = List.of(
                partnerData(editor.getUsername(), PartnerPermission.UPDATE),
                partnerData(viewer.getUsername(), PartnerPermission.VIEW));

        partnerClient.setPartners(adminToken, problemJid, data);

        response = partnerClient.getPartners(adminToken, problemJid);
        assertThat(response.getData()).containsExactlyInAnyOrder(
                partner(editor.getJid(), PartnerPermission.UPDATE),
                partner(viewer.getJid(), PartnerPermission.VIEW));
        assertThat(response.getProfilesMap()).containsOnlyKeys(editor.getJid(), viewer.getJid());

        assertBadRequest(() -> partnerClient.setPartners(adminToken, problemJid, List.of(
                partnerData(editor.getUsername(), PartnerPermission.UPDATE),
                partnerData("nonexistent", PartnerPermission.VIEW))))
                .hasMessageContaining(PARTNER_USERNAMES_NOT_FOUND);

        assertBadRequest(() -> partnerClient.setPartners(adminToken, problemJid, List.of(
                partnerData(editor.getUsername(), PartnerPermission.UPDATE),
                partnerData(editor.getUsername(), PartnerPermission.VIEW))));

        // as partners

        ProblemPermissions permissions = problemClient.getProblemPermissions(editorToken, problemJid);
        assertThat(permissions.getCanEdit()).isTrue();
        assertThat(permissions.getCanManage()).isFalse();

        permissions = problemClient.getProblemPermissions(viewerToken, problemJid);
        assertThat(permissions.getCanEdit()).isFalse();
        assertThat(permissions.getCanManage()).isFalse();

        assertForbidden(() -> partnerClient.getPartners(editorToken, problemJid));
        assertForbidden(() -> partnerClient.setPartners(editorToken, problemJid, data));

        // as admin

        partnerClient.setPartners(adminToken, problemJid, List.of(
                partnerData(viewer.getUsername(), PartnerPermission.UPDATE)));

        response = partnerClient.getPartners(adminToken, problemJid);
        assertThat(response.getData()).containsExactly(partner(viewer.getJid(), PartnerPermission.UPDATE));

        assertForbidden(() -> problemClient.getProblem(editorToken, problemJid));
        assertThat(problemClient.getProblemPermissions(viewerToken, problemJid).getCanEdit()).isTrue();

        // as user

        assertForbidden(() -> partnerClient.getPartners(userToken, problemJid));
        assertForbidden(() -> partnerClient.setPartners(userToken, problemJid, data));
    }

    private static ProblemPartnerData partnerData(String username, PartnerPermission permission) {
        return new ProblemPartnerData.Builder().username(username).permission(permission).build();
    }

    private static Partner partner(String userJid, PartnerPermission permission) {
        return new Partner.Builder().userJid(userJid).permission(permission).build();
    }
}
