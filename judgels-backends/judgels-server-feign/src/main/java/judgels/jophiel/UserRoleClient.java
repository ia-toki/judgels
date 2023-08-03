package judgels.jophiel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Map;
import judgels.jophiel.api.user.role.UserRole;
import judgels.jophiel.api.user.role.UserRolesResponse;

public interface UserRoleClient {
    @RequestLine("GET /api/v2/user-roles")
    @Headers("Authorization: Bearer {token}")
    UserRolesResponse getUserRoles(@Param("token") String token);

    @RequestLine("PUT /api/v2/user-roles")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setUserRoles(@Param("token") String token, Map<String, UserRole> usernameToRoleMap);
}
