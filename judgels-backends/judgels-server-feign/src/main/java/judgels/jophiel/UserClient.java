package judgels.jophiel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UsersResponse;
import judgels.jophiel.api.user.UsersUpsertResponse;

public interface UserClient {
    @RequestLine("GET /api/v2/users")
    @Headers("Authorization: Bearer {token}")
    UsersResponse getUsers(@Param("token") String token);

    @RequestLine("POST /api/v2/users")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    User createUser(@Param("token") String token, UserData data);

    @RequestLine("POST /api/v2/users/batch-upsert")
    @Headers({"Authorization: Bearer {token}", "Content-Type: text/plain"})
    UsersUpsertResponse upsertUsers(@Param("token") String token, String csv);

    @RequestLine("GET /api/v2/users/{userJid}")
    @Headers("Authorization: Bearer {token}")
    User getUser(@Param("token") String token, @Param("userJid") String userJid);

    @RequestLine("GET /api/v2/users/me")
    @Headers("Authorization: Bearer {token}")
    User getMyself(@Param("token") String token);

    @RequestLine("POST /api/v2/users/batch-get")
    @Headers("Content-Type: application/json")
    String exportUsers(List<String> usernames);

    @RequestLine("POST /api/v2/users/batch-get")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    String exportUsers(@Param("token") String token, List<String> usernames);
}
