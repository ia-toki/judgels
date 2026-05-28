package tlx.problemset;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import tlx.api.problemset.ProblemSet;
import tlx.api.problemset.ProblemSetCreateData;
import tlx.api.problemset.ProblemSetUpdateData;

public interface ProblemSetAdminClient {
    @RequestLine("POST /api/v2/admin/problemsets")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ProblemSet createProblemSet(@Param("token") String token, ProblemSetCreateData data);

    @RequestLine("POST /api/v2/admin/problemsets/{problemSetJid}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ProblemSet updateProblemSet(
            @Param("token") String token,
            @Param("problemSetJid") String problemSetJid,
            ProblemSetUpdateData data);
}
