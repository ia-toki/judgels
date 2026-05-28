package tlx.problemset;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import tlx.api.problemset.problem.ProblemEditorialResponse;
import tlx.api.problemset.problem.ProblemReportResponse;
import tlx.api.problemset.problem.ProblemSetProblem;
import tlx.api.problemset.problem.ProblemSetProblemWorksheet;
import tlx.api.problemset.problem.ProblemSetProblemsResponse;

public interface ProblemSetProblemClient {
    @RequestLine("GET /api/v2/problemsets/{problemSetJid}/problems")
    @Headers("Authorization: Bearer {token}")
    ProblemSetProblemsResponse getProblems(@Param("token") String token, @Param("problemSetJid") String problemSetJid);

    @RequestLine("GET /api/v2/problemsets/{problemSetJid}/problems/{problemAlias}")
    @Headers("Authorization: Bearer {token}")
    ProblemSetProblem getProblem(
            @Param("token") String token,
            @Param("problemSetJid") String problemSetJid,
            @Param("problemAlias") String problemAlias);

    @RequestLine("GET /api/v2/problemsets/{problemSetJid}/problems/{problemAlias}/worksheet")
    @Headers("Authorization: Bearer {token}")
    ProblemSetProblemWorksheet getProblemWorksheet(
            @Param("token") String token,
            @Param("problemSetJid") String problemSetJid,
            @Param("problemAlias") String problemAlias);

    @RequestLine("GET /api/v2/problemsets/{problemSetJid}/problems/{problemAlias}/report")
    @Headers("Authorization: Bearer {token}")
    ProblemReportResponse getProblemReport(
            @Param("token") String token,
            @Param("problemSetJid") String problemSetJid,
            @Param("problemAlias") String problemAlias);

    @RequestLine("GET /api/v2/problemsets/{problemSetJid}/problems/{problemAlias}/editorial")
    @Headers("Authorization: Bearer {token}")
    ProblemEditorialResponse getProblemEditorial(
            @Param("token") String token,
            @Param("problemSetJid") String problemSetJid,
            @Param("problemAlias") String problemAlias);
}
