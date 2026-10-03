package judgels.contest;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.contest.rating.ContestsPendingRatingResponse;

public interface ContestRatingClient {
    @RequestLine("GET /api/v2/contest-rating/pending")
    @Headers("Authorization: Bearer {token}")
    ContestsPendingRatingResponse getContestsPendingRating(@Param("token") String token);
}
