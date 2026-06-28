package judgels.user;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.form.FormData;

public interface UserAvatarClient {
    @RequestLine("GET /api/v2/users/{userJid}/avatar/exists")
    boolean avatarExists(@Param("userJid") String userJid);

    @RequestLine("POST /api/v2/users/{userJid}/avatar")
    @Headers({"Authorization: Bearer {token}", "Content-Type: multipart/form-data"})
    void updateAvatar(@Param("token") String token, @Param("userJid") String userJid, @Param("file") FormData file);

    @RequestLine("DELETE /api/v2/users/{userJid}/avatar")
    @Headers("Authorization: Bearer {token}")
    void deleteAvatar(@Param("token") String token, @Param("userJid") String userJid);
}
