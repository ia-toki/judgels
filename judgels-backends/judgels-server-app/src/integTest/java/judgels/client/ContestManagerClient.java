package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Set;
import judgels.api.contest.manager.ContestManagersAddResponse;
import judgels.api.contest.manager.ContestManagersRemoveResponse;
import judgels.api.contest.manager.ContestManagersResponse;

public interface ContestManagerClient {
    @RequestLine("GET /api/v2/contests/{contestJid}/managers")
    @Headers("Authorization: Bearer {token}")
    ContestManagersResponse getManagers(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("POST /api/v2/contests/{contestJid}/managers/add")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestManagersAddResponse addManagers(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            Set<String> usernames);

    @RequestLine("POST /api/v2/contests/{contestJid}/managers/remove")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestManagersRemoveResponse removeManagers(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            Set<String> usernames);
}
