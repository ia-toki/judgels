package judgels.training.problemset;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import judgels.api.training.problemset.ProblemSet;
import judgels.api.training.problemset.ProblemSetCreateData;
import judgels.api.training.problemset.ProblemSetUpdateData;
import judgels.api.training.problemset.ProblemSetUserProgressesData;
import judgels.api.training.problemset.ProblemSetUserProgressesResponse;
import judgels.api.training.problemset.ProblemSetsResponse;

public interface ProblemSetClient {
    class GetProblemSetsParams {
        public String archiveSlug;
        public String name;
    }

    @RequestLine("GET /api/v2/problemsets")
    @Headers("Authorization: Bearer {token}")
    ProblemSetsResponse getProblemSets(@Param("token") String token, @QueryMap GetProblemSetsParams params);

    @RequestLine("GET /api/v2/problemsets/slug/{problemSetSlug}")
    @Headers("Authorization: Bearer {token}")
    ProblemSet getProblemSetBySlug(@Param("token") String token, @Param("problemSetSlug") String problemSetSlug);

    @RequestLine("GET /api/v2/problemsets/{problemSetJid}/stats")
    @Headers("Authorization: Bearer {token}")
    ProblemSet getProblemSetStats(@Param("token") String token, @Param("problemSetJid") String problemSetJid);

    @RequestLine("GET /api/v2/problemsets/search?contestJid={contestJid}")
    ProblemSet searchProblemSet(@Param("contestJid") String contestJid);

    @RequestLine("POST /api/v2/problemsets/user-progresses")
    @Headers("Content-Type: application/json")
    ProblemSetUserProgressesResponse getProblemSetUserProgresses(ProblemSetUserProgressesData data);

    @RequestLine("POST /api/v2/problemsets")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ProblemSet createProblemSet(@Param("token") String token, ProblemSetCreateData data);

    @RequestLine("POST /api/v2/problemsets/{problemSetJid}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ProblemSet updateProblemSet(
            @Param("token") String token,
            @Param("problemSetJid") String problemSetJid,
            ProblemSetUpdateData data);
}
