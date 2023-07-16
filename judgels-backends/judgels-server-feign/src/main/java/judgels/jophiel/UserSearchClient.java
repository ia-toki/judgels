package judgels.jophiel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Map;
import java.util.Set;

public interface UserSearchClient {
    @RequestLine("GET /api/v2/user-search/username-exists/{username}")
    boolean usernameExists(@Param("username") String username);

    @RequestLine("GET /api/v2/user-search/email-exists/{email}")
    boolean emailExists(@Param("email") String email);

    @RequestLine("POST /api/v2/user-search/username-to-jid")
    @Headers("Content-Type: application/json")
    Map<String, String> translateUsernamesToJids(Set<String> usernames);
}
