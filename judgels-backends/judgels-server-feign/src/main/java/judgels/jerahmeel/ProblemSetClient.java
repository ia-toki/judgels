package judgels.jerahmeel;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.ProblemSetCreateData;
import judgels.jerahmeel.api.problemset.ProblemSetUpdateData;
import judgels.jerahmeel.api.problemset.ProblemSetUserProgressesData;
import judgels.jerahmeel.api.problemset.ProblemSetUserProgressesResponse;
import judgels.jerahmeel.api.problemset.ProblemSetsResponse;

public interface ProblemSetClient {
    class GetProblemSetsParams {
        public String archiveSlug;
        public String name;
    }

    @RequestLine("GET /api/v2/problemsets")
    @Headers("Authorization: Bearer {token}")
    ProblemSetsResponse getProblemSets(@Param("token") String token, @QueryMap GetProblemSetsParams params);

    @RequestLine("POST /api/v2/problemsets")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ProblemSet createProblemSet(@Param("token") String token, ProblemSetCreateData data);

    @RequestLine("GET /api/v2/problemsets/slug/{problemSetSlug}")
    @Headers("Authorization: Bearer {token}")
    ProblemSet getProblemSetBySlug(@Param("token") String token, @Param("problemSetSlug") String problemSetSlug);

    @RequestLine("POST /api/v2/problemsets/{problemSetJid}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ProblemSet updateProblemSet(
            @Param("token") String token,
            @Param("problemSetJid") String problemSetJid,
            ProblemSetUpdateData data);

    @RequestLine("GET /api/v2/problemsets/{problemSetJid}/stats")
    @Headers("Authorization: Bearer {token}")
    ProblemSet getProblemSetStats(@Param("token") String token, @Param("problemSetJid") String problemSetJid);

    @RequestLine("GET /api/v2/problemsets/search?contestJid={contestJid}")
    ProblemSet searchProblemSet(@Param("contestJid") String contestJid);

    @RequestLine("POST /api/v2/problemsets/user-progresses")
    @Headers("Content-Type: application/json")
    ProblemSetUserProgressesResponse getProblemSetUserProgresses(ProblemSetUserProgressesData data);

}
