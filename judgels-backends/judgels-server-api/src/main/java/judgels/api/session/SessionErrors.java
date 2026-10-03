package judgels.api.session;

import jakarta.ws.rs.core.Response.Status;
import judgels.core.api.JudgelsApiException;

public class SessionErrors {
    private SessionErrors() {}

    public static final String USER_MAX_CONCURRENT_SESSIONS_EXCEEDED = "UserMaxConcurrentSessionsExceeded";
    public static final String LOGOUT_DISABLED = "LogoutDisabled";

    public static JudgelsApiException userMaxConcurrentSessionsExceeded() {
        return new JudgelsApiException(Status.FORBIDDEN, USER_MAX_CONCURRENT_SESSIONS_EXCEEDED);
    }

    public static JudgelsApiException logoutDisabled() {
        return new JudgelsApiException(Status.FORBIDDEN, LOGOUT_DISABLED);
    }
}
