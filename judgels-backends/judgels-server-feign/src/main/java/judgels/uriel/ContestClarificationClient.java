package judgels.uriel;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import judgels.uriel.api.contest.clarification.ContestClarification;
import judgels.uriel.api.contest.clarification.ContestClarificationAnswerData;
import judgels.uriel.api.contest.clarification.ContestClarificationData;
import judgels.uriel.api.contest.clarification.ContestClarificationsResponse;

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
