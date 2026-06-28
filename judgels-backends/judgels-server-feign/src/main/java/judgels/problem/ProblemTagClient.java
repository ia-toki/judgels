package judgels.problem;

import feign.RequestLine;
import judgels.api.problem.ProblemTagsResponse;

public interface ProblemTagClient {
    @RequestLine("GET /api/v2/problems/tags")
    ProblemTagsResponse getProblemTags();
}
