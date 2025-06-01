package tlx.jophiel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.jophiel.api.user.User;
import tlx.jophiel.api.user.account.UserRegistrationData;

public interface TlxUserAccountClient {
    @RequestLine("POST /api/v2/user-account/register")
    @Headers("Content-Type: application/json")
    User registerUser(UserRegistrationData data);

    @RequestLine("POST /api/v2/user-account/activate/{emailCode}")
    void activateUser(@Param("emailCode") String emailCode);

    @RequestLine("POST /api/v2/user-account/resend-activation-email/{email}")
    void resendActivationEmail(@Param("email") String email);
}
