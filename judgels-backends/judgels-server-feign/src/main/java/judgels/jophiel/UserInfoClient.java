package judgels.jophiel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.jophiel.api.user.info.UserInfo;

public interface UserInfoClient {
    @RequestLine("GET /api/v2/users/{userJid}/info")
    @Headers("Authorization: Bearer {token}")
    UserInfo getInfo(@Param("token") String token, @Param("userJid") String userJid);

    @RequestLine("PUT /api/v2/users/{userJid}/info")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    UserInfo updateInfo(@Param("token") String token, @Param("userJid") String userJid, UserInfo userInfo);
}
