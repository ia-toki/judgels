package tlx.user;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.user.User;
import tlx.api.user.account.UserRegistrationData;

public interface UserAccountWithRegistrationClient {
    @RequestLine("POST /api/v2/user-account/register")
    @Headers("Content-Type: application/json")
    User registerUser(UserRegistrationData data);

    @RequestLine("POST /api/v2/user-account/activate/{emailCode}")
    void activateUser(@Param("emailCode") String emailCode);

    @RequestLine("POST /api/v2/user-account/resend-activation-email/{email}")
    void resendActivationEmail(@Param("email") String email);
}
