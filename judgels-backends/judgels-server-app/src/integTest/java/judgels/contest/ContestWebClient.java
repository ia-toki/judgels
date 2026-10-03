package judgels.contest;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.contest.web.ContestWebConfig;
import judgels.api.contest.web.ContestWithWebConfig;

public interface ContestWebClient {
    @RequestLine("GET /api/v2/contest-web/{contestJid}/config")
    @Headers("Authorization: Bearer {token}")
    ContestWebConfig getWebConfig(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("GET /api/v2/contest-web/{contestJid}/config")
    @Headers("Authorization: Bearer {token}")
    ContestWithWebConfig getContestWithWebConfig(@Param("token") String token, @Param("contestJid") String contestJid);

}
