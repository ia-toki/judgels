package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.training.account.registration.AccountRegistrationData;
import judgels.api.user.User;

public interface AccountRegistrationClient {
    @RequestLine("POST /api/v4/account/registration")
    @Headers("Content-Type: application/json")
    User registerUser(AccountRegistrationData data);

    @RequestLine("POST /api/v4/account/registration/activate/{emailCode}")
    void activateUser(@Param("emailCode") String emailCode);

    @RequestLine("POST /api/v4/account/registration/resend-activation-email/{email}")
    void resendActivationEmail(@Param("email") String email);
}
