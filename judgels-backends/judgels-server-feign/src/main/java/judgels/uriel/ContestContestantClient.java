package judgels.uriel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Set;
import judgels.uriel.api.contest.contestant.ApprovedContestContestantsResponse;
import judgels.uriel.api.contest.contestant.ContestContestantState;
import judgels.uriel.api.contest.contestant.ContestContestantsDeleteResponse;
import judgels.uriel.api.contest.contestant.ContestContestantsResponse;
import judgels.uriel.api.contest.contestant.ContestContestantsUpsertResponse;

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

    @RequestLine("POST /api/v2/contests/{contestJid}/contestants/batch-upsert")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestContestantsUpsertResponse upsertContestants(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            Set<String> usernames);

    @RequestLine("POST /api/v2/contests/{contestJid}/contestants/batch-delete")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestContestantsDeleteResponse deleteContestants(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            Set<String> usernames);
}
