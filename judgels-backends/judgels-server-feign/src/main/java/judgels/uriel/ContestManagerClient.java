package judgels.uriel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Set;
import judgels.uriel.api.contest.manager.ContestManagersDeleteResponse;
import judgels.uriel.api.contest.manager.ContestManagersResponse;
import judgels.uriel.api.contest.manager.ContestManagersUpsertResponse;

public interface ContestManagerClient {
    @RequestLine("GET /api/v2/contests/{contestJid}/managers")
    @Headers("Authorization: Bearer {token}")
    ContestManagersResponse getManagers(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("POST /api/v2/contests/{contestJid}/managers/batch-upsert")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestManagersUpsertResponse upsertManagers(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            Set<String> usernames);

    @RequestLine("POST /api/v2/contests/{contestJid}/managers/batch-delete")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestManagersDeleteResponse deleteManagers(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            Set<String> usernames);
}
