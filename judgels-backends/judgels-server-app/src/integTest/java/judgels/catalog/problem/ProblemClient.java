package judgels.catalog.problem;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import java.util.Set;
import judgels.api.catalog.problem.ProblemsResponse;

public interface ProblemClient {
    class GetProblemsParams {
        public String term;
        public Set<String> tags;
    }

    @RequestLine("GET /api/v4/problems")
    @Headers("Authorization: Bearer {token}")
    ProblemsResponse getProblems(@Param("token") String token, @QueryMap GetProblemsParams params);
}
