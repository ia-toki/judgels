package judgels.user;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Map;
import judgels.api.user.role.UserRole;
import judgels.api.user.role.UserRolesResponse;

public interface UserRoleClient {
    @RequestLine("GET /api/v2/admin/user-roles")
    @Headers("Authorization: Bearer {token}")
    UserRolesResponse getUserRoles(@Param("token") String token);

    @RequestLine("PUT /api/v2/admin/user-roles")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setUserRoles(@Param("token") String token, Map<String, UserRole> usernameToRoleMap);
}
