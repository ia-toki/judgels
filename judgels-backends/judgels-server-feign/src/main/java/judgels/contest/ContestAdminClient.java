package judgels.contest;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.contest.Contest;
import judgels.api.contest.ContestCreateData;

public interface ContestAdminClient {
    @RequestLine("POST /api/v2/admin/contests")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Contest createContest(@Param("token") String token, ContestCreateData data);
}
