package judgels.training.submission;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import feign.form.FormData;
import judgels.api.submission.programming.Submission;
import judgels.api.submission.programming.SubmissionWithSourceResponse;
import judgels.api.training.submission.programming.TrainingSubmissionsResponse;

public interface TrainingSubmissionClient {
    class GetSubmissionsParams {
        public String containerJid;
        public String username;
        public String problemJid;
        public String problemAlias;
    }

    @RequestLine("GET /api/v4/training/submissions/programming")
    @Headers("Authorization: Bearer {token}")
    TrainingSubmissionsResponse getSubmissions(@Param("token") String token, @QueryMap GetSubmissionsParams params);

    @RequestLine("GET /api/v4/training/submissions/programming/{submissionJid}")
    Submission getSubmission(@Param("submissionJid") String submissionJid);

    @RequestLine("GET /api/v4/training/submissions/programming/id/{submissionId}")
    @Headers("Authorization: Bearer {token}")
    SubmissionWithSourceResponse getSubmissionWithSourceById(
            @Param("token") String token,
            @Param("submissionId") long submissionId);

    @RequestLine("POST /api/v4/training/submissions/programming")
    @Headers({"Authorization: Bearer {token}", "Content-Type: multipart/form-data"})
    Submission createSubmission(
            @Param("token") String token,
            @Param("containerJid") String containerJid,
            @Param("problemJid") String problemJid,
            @Param("gradingLanguage") String gradingLanguage,
            @Param("sourceFiles.source") FormData file);

    @RequestLine("POST /api/v4/training/submissions/programming/{submissionJid}/regrade")
    @Headers("Authorization: Bearer {token}")
    void regradeSubmission(@Param("token") String token, @Param("submissionJid") String submissionJid);

    @RequestLine("POST /api/v4/training/submissions/programming/regrade")
    @Headers("Authorization: Bearer {token}")
    void regradeSubmissions(@Param("token") String token, @QueryMap GetSubmissionsParams params);
}
