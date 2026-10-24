package judgels.api;

import static judgels.api.catalog.problem.ProblemErrors.PARTNER_USERNAMES_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.problem.ProblemPermissions;
import judgels.api.catalog.problem.partner.ProblemPartner;
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
        User andi = createUser("andi");
        String andiToken = getToken(andi);
        User budi = createUser("budi");
        String budiToken = getToken(budi);

        String problemJid = createProblem(adminToken, "problem").getJid();

        // as admin

        ProblemPartnersResponse response = partnerClient.getPartners(adminToken, problemJid);
        assertThat(response.getData()).isEmpty();
        assertThat(response.getProfilesMap()).isEmpty();

        List<ProblemPartnerData> data = List.of(partnerData(andi.getUsername()), partnerData(budi.getUsername()));

        partnerClient.setPartners(adminToken, problemJid, data);

        response = partnerClient.getPartners(adminToken, problemJid);
        assertThat(response.getData()).containsExactlyInAnyOrder(partner(andi.getJid()), partner(budi.getJid()));
        assertThat(response.getProfilesMap()).containsOnlyKeys(andi.getJid(), budi.getJid());

        assertBadRequest(() -> partnerClient.setPartners(adminToken, problemJid, List.of(
                partnerData(andi.getUsername()),
                partnerData("nonexistent"))))
                .hasMessageContaining(PARTNER_USERNAMES_NOT_FOUND);

        assertBadRequest(() -> partnerClient.setPartners(adminToken, problemJid, List.of(
                partnerData(andi.getUsername()),
                partnerData(andi.getUsername()))));

        // as partner

        ProblemPermissions permissions = problemClient.getProblemPermissions(andiToken, problemJid);
        assertThat(permissions.getCanManage()).isFalse();
        assertThat(permissions.getReasonNotAllowedToSubmit()).isEmpty();

        assertForbidden(() -> partnerClient.getPartners(andiToken, problemJid));
        assertForbidden(() -> partnerClient.setPartners(andiToken, problemJid, data));

        // as admin

        partnerClient.setPartners(adminToken, problemJid, List.of(partnerData(budi.getUsername())));

        response = partnerClient.getPartners(adminToken, problemJid);
        assertThat(response.getData()).containsExactly(partner(budi.getJid()));

        assertForbidden(() -> problemClient.getProblem(andiToken, problemJid));
        assertPermitted(() -> problemClient.getProblem(budiToken, problemJid));

        // as user

        assertForbidden(() -> partnerClient.getPartners(userToken, problemJid));
        assertForbidden(() -> partnerClient.setPartners(userToken, problemJid, data));
    }

    private static ProblemPartnerData partnerData(String username) {
        return new ProblemPartnerData.Builder().username(username).build();
    }

    private static ProblemPartner partner(String userJid) {
        return new ProblemPartner.Builder().userJid(userJid).build();
    }
}
