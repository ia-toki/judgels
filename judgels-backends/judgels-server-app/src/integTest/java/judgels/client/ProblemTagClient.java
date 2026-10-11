package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Set;
import judgels.api.catalog.problem.tag.ProblemTagsResponse;

public interface ProblemTagClient {
    @RequestLine("GET /api/v4/problems/tags")
    @Headers("Authorization: Bearer {token}")
    ProblemTagsResponse getTags(@Param("token") String token);

    @RequestLine("GET /api/v4/problems/{problemJid}/topic-tags")
    @Headers("Authorization: Bearer {token}")
    Set<String> getTopicTags(@Param("token") String token, @Param("problemJid") String problemJid);

    @RequestLine("PUT /api/v4/problems/{problemJid}/topic-tags")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setTopicTags(@Param("token") String token, @Param("problemJid") String problemJid, Set<String> topicTags);
}
