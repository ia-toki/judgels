package judgels.client;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import java.util.Map;
import judgels.api.submission.bundle.ItemSubmission;
import judgels.api.submission.bundle.ItemSubmissionData;
import judgels.api.training.submission.bundle.TrainingItemSubmissionsResponse;
import judgels.api.training.submission.bundle.TrainingSubmissionSummaryResponse;

public interface TrainingItemSubmissionClient {
    class GetItemSubmissionsParams {
        public String username;
        public String problemAlias;
    }

    @RequestLine("GET /api/v4/training/submissions/bundle?containerJid={containerJid}")
    @Headers("Authorization: Bearer {token}")
    TrainingItemSubmissionsResponse getItemSubmissions(
            @Param("token") String token,
            @Param("containerJid") String containerJid,
            @QueryMap GetItemSubmissionsParams params);

    @RequestLine("POST /api/v4/training/submissions/bundle")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void createItemSubmission(@Param("token") String token, ItemSubmissionData data);

    class GetLatestItemSubmissionsParams {
        public String username;
    }

    @RequestLine("GET /api/v4/training/submissions/bundle/answers?containerJid={containerJid}&problemAlias={problemAlias}")
    @Headers("Authorization: Bearer {token}")
    Map<String, ItemSubmission> getLatestItemSubmissions(
            @Param("token") String token,
            @Param("containerJid") String containerJid,
            @Param("problemAlias") String problemAlias,
            @QueryMap GetLatestItemSubmissionsParams params);

    class GetItemSubmissionSummaryParams {
        public String problemJid;
        public String username;
        public String problemAlias;
    }

    @RequestLine("GET /api/v4/training/submissions/bundle/summary?containerJid={containerJid}")
    @Headers("Authorization: Bearer {token}")
    TrainingSubmissionSummaryResponse getItemSubmissionSummary(
            @Param("token") String token,
            @Param("containerJid") String containerJid,
            @QueryMap GetItemSubmissionSummaryParams params);

    @RequestLine("POST /api/v4/training/submissions/bundle/{submissionJid}/regrade")
    @Headers("Authorization: Bearer {token}")
    void regradeItemSubmission(@Param("token") String token, @Param("submissionJid") String submissionJid);

    class RegradeItemSubmissionsParams {
        public String containerJid;
        public String userJid;
        public String problemJid;
    }

    @RequestLine("POST /api/v4/training/submissions/bundle/regrade")
    @Headers("Authorization: Bearer {token}")
    void regradeItemSubmissions(@Param("token") String token, @QueryMap RegradeItemSubmissionsParams params);
}
