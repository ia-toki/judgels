package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Set;
import judgels.api.contest.supervisor.ContestSupervisorsAddData;
import judgels.api.contest.supervisor.ContestSupervisorsAddResponse;
import judgels.api.contest.supervisor.ContestSupervisorsRemoveResponse;
import judgels.api.contest.supervisor.ContestSupervisorsResponse;

public interface ContestSupervisorClient {
    @RequestLine("GET /api/v2/contests/{contestJid}/supervisors")
    @Headers("Authorization: Bearer {token}")
    ContestSupervisorsResponse getSupervisors(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("POST /api/v2/contests/{contestJid}/supervisors/add")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestSupervisorsAddResponse addSupervisors(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            ContestSupervisorsAddData data);

    @RequestLine("POST /api/v2/contests/{contestJid}/supervisors/remove")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestSupervisorsRemoveResponse removeSupervisors(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            Set<String> usernames);
}
