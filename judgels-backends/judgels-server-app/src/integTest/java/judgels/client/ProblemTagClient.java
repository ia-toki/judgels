package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.catalog.problem.tag.ProblemTagsResponse;

public interface ProblemTagClient {
    @RequestLine("GET /api/v4/problems/tags")
    @Headers("Authorization: Bearer {token}")
    ProblemTagsResponse getTags(@Param("token") String token);
}
