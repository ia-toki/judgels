package judgels.uriel.api.contest;

import com.palantir.conjure.java.api.errors.ErrorType;
import com.palantir.conjure.java.api.errors.ServiceException;
import com.palantir.logsafe.SafeArg;

public class ContestErrors {
    private ContestErrors() {}

    public static final ErrorType CONTEST_SLUG_ALREADY_EXISTS =
            ErrorType.create(ErrorType.Code.INVALID_ARGUMENT, "Uriel:ContestSlugAlreadyExists");

    public static ServiceException contestSlugAlreadyExists(String slug) {
        return new ServiceException(CONTEST_SLUG_ALREADY_EXISTS, SafeArg.of("slug", slug));
    }
}
