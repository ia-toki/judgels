package judgels.jophiel.api.session;

import com.palantir.conjure.java.api.errors.ErrorType;
import com.palantir.conjure.java.api.errors.ServiceException;
import com.palantir.logsafe.SafeArg;

public class SessionErrors {
    private SessionErrors() {}

    public static final ErrorType USER_NOT_ACTIVATED =
            ErrorType.create(ErrorType.Code.PERMISSION_DENIED, "Jophiel:UserNotActivated");

    public static final ErrorType USER_MAX_CONCURRENT_SESSIONS_EXCEEDED =
            ErrorType.create(ErrorType.Code.PERMISSION_DENIED, "Jophiel:UserMaxConcurrentSessionsExceeded");

    public static final ErrorType LOGOUT_DISABLED =
            ErrorType.create(ErrorType.Code.PERMISSION_DENIED, "Jophiel:LogoutDisabled");

    public static ServiceException userNotActivated(String email) {
        return new ServiceException(USER_NOT_ACTIVATED, SafeArg.of("email", email));
    }

    public static ServiceException userMaxConcurrentSessionsExceeded(String username, int maxConcurrentSessions) {
        return new ServiceException(USER_MAX_CONCURRENT_SESSIONS_EXCEEDED,
                SafeArg.of("username", username),
                SafeArg.of("maxConcurrentSessions", Integer.toString(maxConcurrentSessions)));
    }

    public static ServiceException logoutDisabled() {
        return new ServiceException(LOGOUT_DISABLED);
    }
}
