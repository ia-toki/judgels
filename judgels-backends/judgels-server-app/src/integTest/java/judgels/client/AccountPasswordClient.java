package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.training.account.password.AccountPasswordResetData;

public interface AccountPasswordClient {
    @RequestLine("POST /api/v4/account/password/request-reset/{email}")
    void requestToResetPassword(@Param("email") String email);

    @RequestLine("POST /api/v4/account/password/reset")
    @Headers("Content-Type: application/json")
    void resetPassword(AccountPasswordResetData data);
}
