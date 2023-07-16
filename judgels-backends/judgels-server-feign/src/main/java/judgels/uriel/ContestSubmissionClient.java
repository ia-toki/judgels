package judgels.uriel;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import feign.Response;
import feign.form.FormData;
import judgels.sandalphon.api.submission.programming.SubmissionInfo;
import judgels.sandalphon.api.submission.programming.SubmissionWithSourceResponse;
import judgels.uriel.api.contest.submission.programming.ContestSubmissionsResponse;

public interface ContestSubmissionClient {
    class GetSubmissionsParams {
        public String username;
        public String problemAlias;
    }

    @RequestLine("GET /api/v2/contests/submissions/programming?contestJid={contestJid}")
    @Headers("Authorization: Bearer {token}")
    ContestSubmissionsResponse getSubmissions(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @QueryMap GetSubmissionsParams params);

    @RequestLine("GET /api/v2/contests/submissions/programming/id/{submissionId}")
    @Headers("Authorization: Bearer {token}")
    SubmissionWithSourceResponse getSubmissionWithSourceById(@Param("token") String token, @Param("submissionId") long submissionId);

    @RequestLine("GET /api/v2/contests/submissions/programming/info?contestJid={contestJid}&userJid={userJid}&problemJid={problemJid}")
    SubmissionInfo getSubmissionInfo(
            @Param("contestJid") String contestJid,
            @Param("userJid") String userJid,
            @Param("problemJid") String problemJid);

    @RequestLine("GET /api/v2/contests/submissions/programming/image?contestJid={contestJid}&userJid={userJid}&problemJid={problemJid}")
    Response getSubmissionSourceImage(
            @Param("contestJid") String contestJid,
            @Param("userJid") String userJid,
            @Param("problemJid") String problemJid);

    @RequestLine("GET /api/v2/contests/submissions/programming/image/dark?contestJid={contestJid}&userJid={userJid}&problemJid={problemJid}")
    Response getSubmissionSourceDarkImage(
            @Param("contestJid") String contestJid,
            @Param("userJid") String userJid,
            @Param("problemJid") String problemJid);

    @RequestLine("POST /api/v2/contests/submissions/programming")
    @Headers({"Authorization: Bearer {token}", "Content-Type: multipart/form-data"})
    void createSubmission(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @Param("problemJid") String problemJid,
            @Param("gradingLanguage") String gradingLanguage,
            @Param("sourceFiles.source") FormData file);

    @RequestLine("POST /api/v2/contests/submissions/programming/regrade?contestJid={contestJid}")
    @Headers("Authorization: Bearer {token}")
    void regradeSubmissions(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @QueryMap GetSubmissionsParams params);

    @RequestLine("GET /api/v2/contests/submissions/programming/{submissionJid}/download")
    @Headers("Authorization: Bearer {token}")
    Response downloadSubmission(
            @Param("token") String token,
            @Param("submissionJid") String submissionJid);

    class DownloadSubmissionsParams {
        public String userJid;
        public String problemJid;
        public long lastSubmissionId;
        public int limit;
    }

    @RequestLine("GET /api/v2/contests/submissions/programming/download")
    @Headers("Authorization: Bearer {token}")
    Response downloadSubmissions(
            @Param("token") String token,
            @QueryMap DownloadSubmissionsParams params);
}
