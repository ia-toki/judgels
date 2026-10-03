package judgels.api.session;

import jakarta.ws.rs.core.Response.Status;
import java.util.HashMap;
import java.util.Map;
import judgels.core.api.JudgelsApiException;

public class SessionWithRegistrationErrors {
    private SessionWithRegistrationErrors() {}

    public static final String USER_NOT_ACTIVATED = "UserNotActivated";

    public static JudgelsApiException userNotActivated(String email) {
        Map<String, Object> args = new HashMap<>();
        args.put("email", email);
        return new JudgelsApiException(Status.FORBIDDEN, USER_NOT_ACTIVATED, args);
    }
}
