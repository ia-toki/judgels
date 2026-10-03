package judgels.contest;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import judgels.api.contest.clarification.ContestClarification;
import judgels.api.contest.clarification.ContestClarificationAnswerData;
import judgels.api.contest.clarification.ContestClarificationData;
import judgels.api.contest.clarification.ContestClarificationsResponse;

public interface ContestClarificationClient {
    class GetClarificationsParams {
        public String status;
    }

    @RequestLine("GET /api/v2/contests/{contestJid}/clarifications?status={status}")
    @Headers("Authorization: Bearer {token}")
    ContestClarificationsResponse getClarifications(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @QueryMap GetClarificationsParams params);

    @RequestLine("POST /api/v2/contests/{contestJid}/clarifications")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestClarification createClarification(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            ContestClarificationData data);

    @RequestLine("PUT /api/v2/contests/{contestJid}/clarifications/{clarificationJid}/answer")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestClarification answerClarification(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @Param("clarificationJid") String clarificationJid,
            ContestClarificationAnswerData data);
}
