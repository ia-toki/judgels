package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.user.role.UserRole;
import judgels.api.user.role.UserRolesResponse;
import judgels.api.user.role.UserRolesUpsertData;
import judgels.api.user.role.UserRolesUpsertResponse;

public interface UserRoleClient {
    @RequestLine("GET /api/v2/user-roles")
    @Headers("Authorization: Bearer {token}")
    UserRolesResponse getRoles(@Param("token") String token);

    @RequestLine("POST /api/v2/user-roles/batch-upsert")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    UserRolesUpsertResponse upsertRoles(@Param("token") String token, UserRolesUpsertData data);

    @RequestLine("PUT /api/v2/user-roles/{userJid}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setRole(@Param("token") String token, @Param("userJid") String userJid, UserRole role);
}
