package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.catalog.problem.bundle.Item;
import judgels.api.catalog.problem.bundle.item.ProblemItemCreateData;
import judgels.api.catalog.problem.bundle.item.ProblemItemUpdateData;
import judgels.api.catalog.problem.bundle.item.ProblemItemsResponse;

public interface ProblemItemClient {
    @RequestLine("GET /api/v4/problems/{problemJid}/items?language={language}")
    @Headers("Authorization: Bearer {token}")
    ProblemItemsResponse getItems(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("language") String language);

    @RequestLine("POST /api/v4/problems/{problemJid}/items")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Item createItem(@Param("token") String token, @Param("problemJid") String problemJid, ProblemItemCreateData data);

    @RequestLine("GET /api/v4/problems/{problemJid}/items/{itemJid}?language={language}")
    @Headers("Authorization: Bearer {token}")
    Item getItem(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("itemJid") String itemJid,
            @Param("language") String language);

    @RequestLine("PUT /api/v4/problems/{problemJid}/items/{itemJid}?language={language}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void updateItem(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("itemJid") String itemJid,
            @Param("language") String language,
            ProblemItemUpdateData data);

    @RequestLine("POST /api/v4/problems/{problemJid}/items/{itemJid}/move-up")
    @Headers("Authorization: Bearer {token}")
    void moveItemUp(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("itemJid") String itemJid);

    @RequestLine("POST /api/v4/problems/{problemJid}/items/{itemJid}/move-down")
    @Headers("Authorization: Bearer {token}")
    void moveItemDown(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("itemJid") String itemJid);

    @RequestLine("DELETE /api/v4/problems/{problemJid}/items/{itemJid}")
    @Headers("Authorization: Bearer {token}")
    void deleteItem(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("itemJid") String itemJid);
}
