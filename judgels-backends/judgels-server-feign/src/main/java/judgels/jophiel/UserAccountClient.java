package judgels.jophiel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.jophiel.api.user.account.PasswordResetData;

public interface UserAccountClient {
    @RequestLine("POST /api/v2/user-account/request-reset-password/{email}")
    void requestToResetPassword(@Param("email") String email);

    @RequestLine("POST /api/v2/user-account/reset-password")
    @Headers("Content-Type: application/json")
    void resetPassword(PasswordResetData data);
}
