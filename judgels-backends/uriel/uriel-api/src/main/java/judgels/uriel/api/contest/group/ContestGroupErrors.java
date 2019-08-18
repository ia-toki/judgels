package judgels.uriel.api.contest.group;

import com.palantir.conjure.java.api.errors.ErrorType;
import com.palantir.conjure.java.api.errors.ServiceException;
import com.palantir.logsafe.SafeArg;

public class ContestGroupErrors {
    private ContestGroupErrors() {}

    public static final ErrorType SLUG_ALREADY_EXISTS =
            ErrorType.create(ErrorType.Code.INVALID_ARGUMENT, "Uriel:ContestGroupSlugAlreadyExists");

    public static ServiceException slugAlreadyExists(String slug) {
        return new ServiceException(SLUG_ALREADY_EXISTS, SafeArg.of("slug", slug));
    }
}
