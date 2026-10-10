package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.user.web.UserWebConfig;

public interface UserWebClient {
    @RequestLine("GET /api/v2/user-web/config")
    UserWebConfig getWebConfig();

    @RequestLine("GET /api/v2/user-web/config")
    @Headers("Authorization: Bearer {token}")
    UserWebConfig getWebConfig(@Param("token") String token);
}
