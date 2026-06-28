package judgels.user;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.user.web.UserWebConfig;

public interface UserWebClient {
    @RequestLine("GET /api/v2/user-web/config")
    UserWebConfig getPublicWebConfig();

    @RequestLine("GET /api/v2/user-web/config")
    @Headers("Authorization: Bearer {token}")
    UserWebConfig getWebConfig(@Param("token") String token);
}
