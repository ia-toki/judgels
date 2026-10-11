package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.submission.bundle.BundleAnswer;
import judgels.api.submission.bundle.ProblemBundleSubmissionWithAnswerResponse;
import judgels.api.submission.bundle.ProblemBundleSubmissionsResponse;

public interface ProblemBundleSubmissionClient {
    @RequestLine("GET /api/v4/problems/{problemJid}/submissions/bundle")
    @Headers("Authorization: Bearer {token}")
    ProblemBundleSubmissionsResponse getBundleSubmissions(
            @Param("token") String token,
            @Param("problemJid") String problemJid);

    @RequestLine("GET /api/v4/problems/{problemJid}/submissions/bundle/id/{submissionId}")
    @Headers("Authorization: Bearer {token}")
    ProblemBundleSubmissionWithAnswerResponse getBundleSubmissionWithAnswerById(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("submissionId") long submissionId);

    @RequestLine("POST /api/v4/problems/{problemJid}/submissions/bundle")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void createBundleSubmission(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            BundleAnswer data);

    @RequestLine("POST /api/v4/problems/{problemJid}/submissions/bundle/{submissionJid}/regrade")
    @Headers("Authorization: Bearer {token}")
    void regradeBundleSubmission(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("submissionJid") String submissionJid);

    @RequestLine("POST /api/v4/problems/{problemJid}/submissions/bundle/regrade")
    @Headers("Authorization: Bearer {token}")
    void regradeBundleSubmissions(@Param("token") String token, @Param("problemJid") String problemJid);
}
