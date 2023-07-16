package judgels.uriel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.uriel.api.contest.ActiveContestsResponse;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestDescription;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.ContestsResponse;

public interface ContestClient {
    @RequestLine("GET /api/v2/contests")
    @Headers("Authorization: Bearer {token}")
    ContestsResponse getContests(@Param("token") String token);

    @RequestLine("GET /api/v2/contests/active")
    @Headers("Authorization: Bearer {token}")
    ActiveContestsResponse getActiveContests(@Param("token") String token);

    @RequestLine("POST /api/v2/contests")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Contest createContest(@Param("token") String token, ContestCreateData data);

    @RequestLine("GET /api/v2/contests/{contestJid}")
    @Headers("Authorization: Bearer {token}")
    Contest getContest(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("GET /api/v2/contests/slug/{contestSlug}")
    @Headers("Authorization: Bearer {token}")
    Contest getContestBySlug(@Param("token") String token, @Param("contestSlug") String contestSlug);

    @RequestLine("GET /api/v2/contests/{contestJid}/description")
    @Headers("Authorization: Bearer {token}")
    ContestDescription getContestDescription(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("POST /api/v2/contests/{contestJid}/description")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestDescription updateContestDescription(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            ContestDescription data);

    @RequestLine("POST /api/v2/contests/{contestJid}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Contest updateContest(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            ContestUpdateData data);

    @RequestLine("POST /api/v2/contests/{contestJid}/virtual")
    @Headers("Authorization: Bearer {token}")
    void startVirtualContest(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("PUT /api/v2/contests/{contestJid}/virtual/reset")
    @Headers("Authorization: Bearer {token}")
    void resetVirtualContest(@Param("token") String token, @Param("contestJid") String contestJid);
}
