package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;
import judgels.api.catalog.problem.partner.ProblemPartnerData;
import judgels.api.catalog.problem.partner.ProblemPartnersResponse;

public interface ProblemPartnerClient {
    @RequestLine("GET /api/v4/problems/{problemJid}/partners")
    @Headers("Authorization: Bearer {token}")
    ProblemPartnersResponse getPartners(@Param("token") String token, @Param("problemJid") String problemJid);

    @RequestLine("PUT /api/v4/problems/{problemJid}/partners")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setPartners(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            List<ProblemPartnerData> data);
}
