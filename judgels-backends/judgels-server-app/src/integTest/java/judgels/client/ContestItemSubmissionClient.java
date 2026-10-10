package judgels.client;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import java.util.Map;
import judgels.api.contest.submission.bundle.ContestItemSubmissionsResponse;
import judgels.api.contest.submission.bundle.ContestSubmissionSummaryResponse;
import judgels.api.submission.bundle.ItemSubmission;
import judgels.api.submission.bundle.ItemSubmissionData;

public interface ContestItemSubmissionClient {
    class GetItemSubmissionsParams {
        public String username;
        public String problemAlias;
    }

    @RequestLine("GET /api/v2/contests/submissions/bundle?contestJid={contestJid}")
    @Headers("Authorization: Bearer {token}")
    ContestItemSubmissionsResponse getItemSubmissions(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @QueryMap GetItemSubmissionsParams params);

    @RequestLine("POST /api/v2/contests/submissions/bundle")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void createItemSubmission(@Param("token") String token, ItemSubmissionData data);

    class GetItemSubmissionSummaryParams {
        public String username;
    }

    @RequestLine("GET /api/v2/contests/submissions/bundle/summary?contestJid={contestJid}")
    @Headers("Authorization: Bearer {token}")
    ContestSubmissionSummaryResponse getItemSubmissionSummary(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @QueryMap GetItemSubmissionSummaryParams params);

    class GetLatestItemSubmissionsParams {
        public String username;
    }

    @RequestLine("GET /api/v2/contests/submissions/bundle/answers?contestJid={contestJid}&problemAlias={problemAlias}")
    @Headers("Authorization: Bearer {token}")
    Map<String, ItemSubmission> getLatestItemSubmissions(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @Param("problemAlias") String problemAlias,
            @QueryMap GetLatestItemSubmissionsParams params);

    @RequestLine("POST /api/v2/contests/submissions/bundle/{submissionJid}/regrade")
    @Headers("Authorization: Bearer {token}")
    void regradeItemSubmission(
            @Param("token") String token,
            @Param("submissionJid") String submissionJid);

    class RegradeItemSubmissionsParams {
        public String contestJid;
        public String username;
        public String problemJid;
        public String problemAlias;
    }

    @RequestLine("POST /api/v2/contests/submissions/bundle/regrade")
    @Headers("Authorization: Bearer {token}")
    void regradeItemSubmissions(
            @Param("token") String token,
            @QueryMap RegradeItemSubmissionsParams params);
}
