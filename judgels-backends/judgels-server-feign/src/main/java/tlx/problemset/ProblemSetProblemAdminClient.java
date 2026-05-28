package tlx.problemset;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;
import tlx.api.problemset.problem.ProblemSetProblemData;

public interface ProblemSetProblemAdminClient {
    @RequestLine("PUT /api/v2/admin/problemsets/{problemSetJid}/problems")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setProblems(
            @Param("token") String token,
            @Param("problemSetJid") String problemSetJid,
            List<ProblemSetProblemData> data);
}
