package judgels.jophiel.api.play;

import com.palantir.conjure.java.api.errors.ErrorType;
import com.palantir.conjure.java.api.errors.ServiceException;

public class PlaySessionErrors {
    private PlaySessionErrors() {}

    public static final ErrorType ROLE_NOT_ALLOWED =
            ErrorType.create(ErrorType.Code.PERMISSION_DENIED, "Jophiel:RoleNotAllowed");

    public static ServiceException roleNotAllowed() {
        return new ServiceException(ROLE_NOT_ALLOWED);
    }
}
