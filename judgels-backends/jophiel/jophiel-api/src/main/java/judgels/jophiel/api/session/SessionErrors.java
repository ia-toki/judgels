package judgels.jophiel.api.session;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response.Status;
import judgels.service.api.JudgelsServiceException;

public class SessionErrors {
    private SessionErrors() {}

    public static final String USER_NOT_ACTIVATED = "Jophiel:UserNotActivated";
    public static final String USER_MAX_CONCURRENT_SESSIONS_EXCEEDED = "Jophiel:UserMaxConcurrentSessionsExceeded";
    public static final String LOGOUT_DISABLED = "Jophiel:LogoutDisabled";

    public static JudgelsServiceException userNotActivated(String email) {
        Map<String, Object> args = new HashMap<>();
        args.put("email", email);
        return new JudgelsServiceException(Status.FORBIDDEN, USER_NOT_ACTIVATED, args);
    }

    public static JudgelsServiceException userMaxConcurrentSessionsExceeded() {
        return new JudgelsServiceException(Status.FORBIDDEN, USER_MAX_CONCURRENT_SESSIONS_EXCEEDED);
    }

    public static JudgelsServiceException logoutDisabled() {
        return new JudgelsServiceException(Status.FORBIDDEN, LOGOUT_DISABLED);
    }
}
