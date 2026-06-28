package judgels.contest;

import feign.Param;
import feign.RequestLine;
import judgels.api.contest.history.ContestHistoryResponse;

public interface ContestHistoryClient {
    @RequestLine("GET /api/v2/contest-history/public?username={username}")
    ContestHistoryResponse getPublicHistory(@Param("username") String username);
}
