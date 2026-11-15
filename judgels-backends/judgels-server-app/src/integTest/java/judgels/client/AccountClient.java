package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.training.account.AccountPasswordResetData;
import judgels.api.training.account.AccountRegistrationData;
import judgels.api.user.User;

public interface AccountClient {
    @RequestLine("POST /api/v4/account/register")
    @Headers("Content-Type: application/json")
    User registerUser(AccountRegistrationData data);

    @RequestLine("POST /api/v4/account/activate/{emailCode}")
    void activateUser(@Param("emailCode") String emailCode);

    @RequestLine("POST /api/v4/account/resend-activation-email/{email}")
    void resendActivationEmail(@Param("email") String email);

    @RequestLine("POST /api/v4/account/request-reset-password/{email}")
    void requestResetPassword(@Param("email") String email);

    @RequestLine("POST /api/v4/account/reset-password")
    @Headers("Content-Type: application/json")
    void resetPassword(AccountPasswordResetData data);
}
