package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Set;
import judgels.api.contest.contestant.ApprovedContestContestantsResponse;
import judgels.api.contest.contestant.ContestContestantState;
import judgels.api.contest.contestant.ContestContestantsAddResponse;
import judgels.api.contest.contestant.ContestContestantsRemoveResponse;
import judgels.api.contest.contestant.ContestContestantsResponse;

public interface ContestContestantClient {
    @RequestLine("GET /api/v2/contests/{contestJid}/contestants")
    @Headers("Authorization: Bearer {token}")
    ContestContestantsResponse getContestants(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("GET /api/v2/contests/{contestJid}/contestants/approved")
    @Headers("Authorization: Bearer {token}")
    ApprovedContestContestantsResponse getApprovedContestants(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("GET /api/v2/contests/{contestJid}/contestants/approved/count")
    @Headers("Authorization: Bearer {token}")
    int getApprovedContestantsCount(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("POST /api/v2/contests/{contestJid}/contestants/me")
    @Headers("Authorization: Bearer {token}")
    void registerMyselfAsContestant(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("DELETE /api/v2/contests/{contestJid}/contestants/me")
    @Headers("Authorization: Bearer {token}")
    void unregisterMyselfAsContestant(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("GET /api/v2/contests/{contestJid}/contestants/me/state")
    @Headers("Authorization: Bearer {token}")
    ContestContestantState getMyContestantState(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("POST /api/v2/contests/{contestJid}/contestants/add")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestContestantsAddResponse addContestants(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            Set<String> usernames);

    @RequestLine("POST /api/v2/contests/{contestJid}/contestants/remove")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestContestantsRemoveResponse removeContestants(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            Set<String> usernames);
}
