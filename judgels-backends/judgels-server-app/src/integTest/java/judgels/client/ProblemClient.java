package judgels.client;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import java.util.Set;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.ProblemCreateData;
import judgels.api.catalog.problem.ProblemResponse;
import judgels.api.catalog.problem.ProblemUpdateData;
import judgels.api.catalog.problem.ProblemsResponse;

public interface ProblemClient {
    class GetProblemsParams {
        public String term;
        public Set<String> tags;
    }

    @RequestLine("GET /api/v4/problems")
    @Headers("Authorization: Bearer {token}")
    ProblemsResponse getProblems(@Param("token") String token, @QueryMap GetProblemsParams params);

    @RequestLine("POST /api/v4/problems")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Problem createProblem(@Param("token") String token, ProblemCreateData data);

    @RequestLine("GET /api/v4/problems/{problemJid}")
    @Headers("Authorization: Bearer {token}")
    ProblemResponse getProblem(@Param("token") String token, @Param("problemJid") String problemJid);

    @RequestLine("GET /api/v4/problems/slug/{problemSlug}")
    @Headers("Authorization: Bearer {token}")
    Problem getProblemBySlug(@Param("token") String token, @Param("problemSlug") String problemSlug);

    @RequestLine("POST /api/v4/problems/{problemJid}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Problem updateProblem(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            ProblemUpdateData data);
}
