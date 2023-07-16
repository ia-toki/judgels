package judgels.uriel;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import java.util.Optional;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardResponse;

public interface ContestScoreboardClient {
    class GetScoreboardParams {
        public boolean frozen;
        public boolean showClosedProblems;
    }

    @RequestLine("GET /api/v2/contests/{contestJid}/scoreboard")
    @Headers("Authorization: Bearer {token}")
    Optional<ContestScoreboardResponse> getScoreboard(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @QueryMap GetScoreboardParams params);

    @RequestLine("POST /api/v2/contests/{contestJid}/scoreboard/refresh")
    @Headers("Authorization: Bearer {token}")
    void refreshScoreboard(@Param("token") String token, @Param("contestJid") String contestJid);
}
