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
    class GetSubmissionsParams {
        public String username;
        public String problemAlias;
    }

    @RequestLine("GET /api/v4/training/submissions/bundle?containerJid={containerJid}")
    @Headers("Authorization: Bearer {token}")
    TrainingItemSubmissionsResponse getSubmissions(
            @Param("token") String token,
            @Param("containerJid") String containerJid,
            @QueryMap GetSubmissionsParams params);

    @RequestLine("POST /api/v4/training/submissions/bundle")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void createItemSubmission(@Param("token") String token, ItemSubmissionData data);

    class GetLatestSubmissionsParams {
        public String username;
    }

    @RequestLine("GET /api/v4/training/submissions/bundle/answers?containerJid={containerJid}&problemAlias={problemAlias}")
    @Headers("Authorization: Bearer {token}")
    Map<String, ItemSubmission> getLatestSubmissions(
            @Param("token") String token,
            @Param("containerJid") String containerJid,
            @Param("problemAlias") String problemAlias,
            @QueryMap GetLatestSubmissionsParams params);

    class GetSubmissionSummaryParams {
        public String problemJid;
        public String username;
        public String problemAlias;
    }

    @RequestLine("GET /api/v4/training/submissions/bundle/summary?containerJid={containerJid}")
    @Headers("Authorization: Bearer {token}")
    TrainingSubmissionSummaryResponse getSubmissionSummary(
            @Param("token") String token,
            @Param("containerJid") String containerJid,
            @QueryMap GetSubmissionSummaryParams params);

    @RequestLine("POST /api/v4/training/submissions/bundle/{submissionJid}/regrade")
    @Headers("Authorization: Bearer {token}")
    void regradeSubmission(@Param("token") String token, @Param("submissionJid") String submissionJid);

    class RegradeSubmissionsParams {
        public String containerJid;
        public String userJid;
        public String problemJid;
    }

    @RequestLine("POST /api/v4/training/submissions/bundle/regrade")
    @Headers("Authorization: Bearer {token}")
    void regradeSubmissions(@Param("token") String token, @QueryMap RegradeSubmissionsParams params);
}
