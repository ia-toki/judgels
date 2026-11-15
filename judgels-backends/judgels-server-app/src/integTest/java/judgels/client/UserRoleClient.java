package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Set;
import judgels.api.user.role.UserRole;
import judgels.api.user.role.UserRolesAddData;
import judgels.api.user.role.UserRolesAddResponse;
import judgels.api.user.role.UserRolesResponse;

public interface UserRoleClient {
    @RequestLine("GET /api/v2/user-roles")
    @Headers("Authorization: Bearer {token}")
    UserRolesResponse getRoles(@Param("token") String token);

    @RequestLine("POST /api/v2/user-roles/add")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    UserRolesAddResponse addRoles(@Param("token") String token, UserRolesAddData data);

    @RequestLine("PUT /api/v2/user-roles/{userJid}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setRoles(@Param("token") String token, @Param("userJid") String userJid, Set<UserRole> roles);
}
