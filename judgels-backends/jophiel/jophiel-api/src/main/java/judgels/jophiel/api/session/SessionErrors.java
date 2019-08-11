package judgels.jophiel.api.session;

import com.palantir.conjure.java.api.errors.ErrorType;
import com.palantir.conjure.java.api.errors.ServiceException;
import com.palantir.logsafe.SafeArg;

public class SessionErrors {
    private SessionErrors() {}

    public static final ErrorType USER_NOT_ACTIVATED =
            ErrorType.create(ErrorType.Code.PERMISSION_DENIED, "Jophiel:UserNotActivated");

    public static ServiceException userNotActivated(String email) {
        return new ServiceException(USER_NOT_ACTIVATED, SafeArg.of("email", email));
    }
}
