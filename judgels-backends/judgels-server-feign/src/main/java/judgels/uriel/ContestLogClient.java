package judgels.uriel;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import judgels.uriel.api.contest.log.ContestLogsResponse;

public interface ContestLogClient {
    class GetLogsParams {
        public String username;
        public String problemAlias;
    }

    @RequestLine("GET /api/v2/contests/{contestJid}/logs")
    @Headers("Authorization: Bearer {token}")
    ContestLogsResponse getLogs(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @QueryMap GetLogsParams params);
}
