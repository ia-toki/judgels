package judgels.client;

import feign.Param;
import feign.RequestLine;
import judgels.api.training.stats.UserStats;
import judgels.api.training.stats.UserTopStatsResponse;

public interface TrainingUserStatsClient {
    @RequestLine("GET /api/v4/training/stats/users/top?page={page}&pageSize={pageSize}")
    UserTopStatsResponse getTopUserStats(@Param("page") int page, @Param("pageSize") int pageSize);

    @RequestLine("GET /api/v4/training/stats/users?username={username}")
    UserStats getUserStats(@Param("username") String username);
}
