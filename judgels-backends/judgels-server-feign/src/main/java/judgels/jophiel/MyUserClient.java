package judgels.jophiel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.jophiel.api.role.UserRole;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.me.PasswordUpdateData;

public interface MyUserClient {
    @RequestLine("GET /api/v2/users/me")
    @Headers("Authorization: Bearer {token}")
    User getMyself(@Param("token") String token);

    @RequestLine("POST /api/v2/users/me/password")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void updateMyPassword(@Param("token") String token, PasswordUpdateData data);

    @RequestLine("GET /api/v2/users/me/role")
    @Headers("Authorization: Bearer {token}")
    UserRole getMyRole(@Param("token") String token);
}
