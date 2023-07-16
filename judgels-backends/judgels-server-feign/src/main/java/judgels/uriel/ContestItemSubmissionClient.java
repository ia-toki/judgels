package judgels.uriel;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import java.util.Map;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.bundle.ItemSubmissionData;
import judgels.uriel.api.contest.submission.bundle.ContestItemSubmissionsResponse;
import judgels.uriel.api.contest.submission.bundle.ContestSubmissionSummaryResponse;

public interface ContestItemSubmissionClient {
    class GetSubmissionsParams {
        public String username;
        public String problemAlias;
    }

    @RequestLine("GET /api/v2/contests/submissions/bundle?contestJid={contestJid}")
    @Headers("Authorization: Bearer {token}")
    ContestItemSubmissionsResponse getSubmissions(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @QueryMap GetSubmissionsParams params);

    @RequestLine("POST /api/v2/contests/submissions/bundle")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void createItemSubmission(@Param("token") String token, ItemSubmissionData data);

    class GetSubmissionSummaryParams {
        public String username;
    }

    @RequestLine("GET /api/v2/contests/submissions/bundle/summary?contestJid={contestJid}")
    @Headers("Authorization: Bearer {token}")
    ContestSubmissionSummaryResponse getSubmissionSummary(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @QueryMap GetSubmissionSummaryParams params);

    class GetLatestSubmissionsParams {
        public String username;
    }

    @RequestLine("GET /api/v2/contests/submissions/bundle/answers?contestJid={contestJid}&problemAlias={problemAlias}")
    @Headers("Authorization: Bearer {token}")
    Map<String, ItemSubmission> getLatestSubmissions(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @Param("problemAlias") String problemAlias,
            @QueryMap GetLatestSubmissionsParams params);

    @RequestLine("POST /api/v2/contests/submissions/bundle/{submissionJid}/regrade")
    @Headers("Authorization: Bearer {token}")
    void regradeSubmissions(
            @Param("token") String token,
            @Param("submissionJid") String submissionJid);

    class RegradeSubmissionsParams {
        public String contestJid;
        public String username;
        public String problemJid;
        public String problemAlias;
    }

    @RequestLine("POST /api/v2/contests/submissions/bundle/regrade")
    @Headers("Authorization: Bearer {token}")
    void regradeSubmissions(
            @Param("token") String token,
            @QueryMap RegradeSubmissionsParams params);
}
