package judgels.contest;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Set;
import judgels.api.contest.supervisor.ContestSupervisorUpsertData;
import judgels.api.contest.supervisor.ContestSupervisorsDeleteResponse;
import judgels.api.contest.supervisor.ContestSupervisorsResponse;
import judgels.api.contest.supervisor.ContestSupervisorsUpsertResponse;

public interface ContestSupervisorClient {
    @RequestLine("GET /api/v2/contests/{contestJid}/supervisors")
    @Headers("Authorization: Bearer {token}")
    ContestSupervisorsResponse getSupervisors(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("POST /api/v2/contests/{contestJid}/supervisors/batch-upsert")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestSupervisorsUpsertResponse upsertSupervisors(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            ContestSupervisorUpsertData data);

    @RequestLine("POST /api/v2/contests/{contestJid}/supervisors/batch-delete")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestSupervisorsDeleteResponse deleteSupervisors(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            Set<String> usernames);
}
