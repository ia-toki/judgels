package judgels.contrib.jophiel.api.session;

import jakarta.ws.rs.core.Response.Status;
import java.util.HashMap;
import java.util.Map;
import judgels.service.api.JudgelsServiceException;

public class SessionWithRegistrationErrors {
    private SessionWithRegistrationErrors() {}

    public static final String USER_NOT_ACTIVATED = "Jophiel:UserNotActivated";

    public static JudgelsServiceException userNotActivated(String email) {
        Map<String, Object> args = new HashMap<>();
        args.put("email", email);
        return new JudgelsServiceException(Status.FORBIDDEN, USER_NOT_ACTIVATED, args);
    }
}
