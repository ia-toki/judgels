package judgels.uriel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Set;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.ContestModulesConfig;

public interface ContestModuleClient {
    @RequestLine("GET /api/v2/contests/{contestJid}/modules")
    @Headers("Authorization: Bearer {token}")
    Set<ContestModuleType> getModules(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("PUT /api/v2/contests/{contestJid}/modules/{type}")
    @Headers("Authorization: Bearer {token}")
    void enableModule(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @Param("type") ContestModuleType type);

    @RequestLine("DELETE /api/v2/contests/{contestJid}/modules/{type}")
    @Headers("Authorization: Bearer {token}")
    void disableModule(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @Param("type") ContestModuleType type);

    @RequestLine("GET /api/v2/contests/{contestJid}/modules/config")
    @Headers("Authorization: Bearer {token}")
    ContestModulesConfig getConfig(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("PUT /api/v2/contests/{contestJid}/modules/config")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void upsertConfig(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            ContestModulesConfig data);
}
