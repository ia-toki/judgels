package judgels.jophiel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.Session;

public interface SessionClient {
    @RequestLine("POST /api/v2/session/login")
    @Headers("Content-Type: application/json")
    Session logIn(Credentials credentials);

    @RequestLine("POST /api/v2/session/logout")
    @Headers("Authorization: Bearer {token}")
    void logOut(@Param("token") String token);
}
