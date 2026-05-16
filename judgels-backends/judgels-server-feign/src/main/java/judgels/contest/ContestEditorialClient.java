package judgels.contest;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.contest.editorial.ContestEditorialResponse;

public interface ContestEditorialClient {
    @RequestLine("GET /api/v2/contests/{contestJid}/editorial")
    @Headers("Authorization: Bearer {token}")
    ContestEditorialResponse getEditorial(@Param("token") String token, @Param("contestJid") String contestJid);
}
