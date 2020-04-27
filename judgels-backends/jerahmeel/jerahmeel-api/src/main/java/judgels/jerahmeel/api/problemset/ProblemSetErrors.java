package judgels.jerahmeel.api.problemset;

import com.palantir.conjure.java.api.errors.ErrorType;
import com.palantir.conjure.java.api.errors.ServiceException;
import com.palantir.logsafe.SafeArg;

public class ProblemSetErrors {
    private ProblemSetErrors() {}

    public static final ErrorType SLUG_ALREADY_EXISTS =
            ErrorType.create(ErrorType.Code.INVALID_ARGUMENT, "Jerahmeel:ProblemSetSlugAlreadyExists");

    public static final ErrorType ARCHIVE_SLUG_NOT_FOUND =
            ErrorType.create(ErrorType.Code.INVALID_ARGUMENT, "Jerahmeel:ArchiveSlugNotFound");

    public static ServiceException slugAlreadyExists(String slug) {
        return new ServiceException(SLUG_ALREADY_EXISTS, SafeArg.of("slug", slug));
    }

    public static ServiceException archiveSlugNotFound(String archiveSlug) {
        return new ServiceException(ARCHIVE_SLUG_NOT_FOUND, SafeArg.of("archiveSlug", archiveSlug));
    }
}
