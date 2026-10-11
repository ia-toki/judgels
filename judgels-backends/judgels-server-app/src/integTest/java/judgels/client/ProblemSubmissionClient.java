package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.form.FormData;
import judgels.api.catalog.problem.programming.ProblemSubmissionConfig;
import judgels.api.submission.programming.ProblemSubmissionWithSourceResponse;
import judgels.api.submission.programming.ProblemSubmissionsResponse;
import judgels.api.submission.programming.Submission;

public interface ProblemSubmissionClient {
    @RequestLine("GET /api/v4/problems/{problemJid}/submissions/programming")
    @Headers("Authorization: Bearer {token}")
    ProblemSubmissionsResponse getSubmissions(@Param("token") String token, @Param("problemJid") String problemJid);

    @RequestLine("GET /api/v4/problems/{problemJid}/submissions/programming/config")
    @Headers("Authorization: Bearer {token}")
    ProblemSubmissionConfig getSubmissionConfig(@Param("token") String token, @Param("problemJid") String problemJid);

    @RequestLine("GET /api/v4/problems/{problemJid}/submissions/programming/id/{submissionId}")
    @Headers("Authorization: Bearer {token}")
    ProblemSubmissionWithSourceResponse getSubmissionWithSourceById(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("submissionId") long submissionId);

    @RequestLine("POST /api/v4/problems/{problemJid}/submissions/programming")
    @Headers({"Authorization: Bearer {token}", "Content-Type: multipart/form-data"})
    Submission createSubmission(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("gradingLanguage") String gradingLanguage,
            @Param("sourceFiles.source") FormData file);

    @RequestLine("POST /api/v4/problems/{problemJid}/submissions/programming/{submissionJid}/regrade")
    @Headers("Authorization: Bearer {token}")
    void regradeSubmission(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("submissionJid") String submissionJid);

    @RequestLine("POST /api/v4/problems/{problemJid}/submissions/programming/regrade")
    @Headers("Authorization: Bearer {token}")
    void regradeSubmissions(@Param("token") String token, @Param("problemJid") String problemJid);
}
