package judgels.uriel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemsResponse;

public interface ContestProblemClient {
    @RequestLine("GET /api/v2/contests/{contestJid}/problems")
    @Headers("Authorization: Bearer {token}")
    ContestProblemsResponse getProblems(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("PUT /api/v2/contests/{contestJid}/problems")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setProblems(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            List<ContestProblemData> data);

    @RequestLine("GET /api/v2/contests/{contestJid}/problems/{problemAlias}/programming/worksheet")
    @Headers("Authorization: Bearer {token}")
    judgels.uriel.api.contest.problem.programming.ContestProblemWorksheet getProgrammingProblemWorksheet(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @Param("problemAlias") String problemAlias);

    @RequestLine("GET /api/v2/contests/{contestJid}/problems/{problemAlias}/bundle/worksheet")
    @Headers("Authorization: Bearer {token}")
    judgels.uriel.api.contest.problem.bundle.ContestProblemWorksheet getBundleProblemWorksheet(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @Param("problemAlias") String problemAlias);
}
