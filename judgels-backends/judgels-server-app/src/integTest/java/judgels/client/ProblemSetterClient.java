package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;
import java.util.Map;
import judgels.api.catalog.problem.ProblemSetterRole;
import judgels.api.catalog.problem.setter.ProblemSettersResponse;

public interface ProblemSetterClient {
    @RequestLine("GET /api/v4/problems/{problemJid}/setters")
    @Headers("Authorization: Bearer {token}")
    ProblemSettersResponse getSetters(@Param("token") String token, @Param("problemJid") String problemJid);

    @RequestLine("PUT /api/v4/problems/{problemJid}/setters")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setSetters(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            Map<ProblemSetterRole, List<String>> setterUsernamesMap);
}
