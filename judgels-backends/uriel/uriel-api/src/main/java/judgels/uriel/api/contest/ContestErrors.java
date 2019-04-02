package judgels.uriel.api.contest;

import com.palantir.conjure.java.api.errors.ErrorType;
import com.palantir.conjure.java.api.errors.ServiceException;
import com.palantir.logsafe.SafeArg;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.sandalphon.api.problem.ProblemType;

public class ContestErrors {
    private ContestErrors() {}

    public static final ErrorType JID_ALREADY_EXISTS =
            ErrorType.create(ErrorType.Code.INVALID_ARGUMENT, "Uriel:ContestJidAlreadyExists");

    public static final ErrorType SLUG_ALREADY_EXISTS =
            ErrorType.create(ErrorType.Code.INVALID_ARGUMENT, "Uriel:ContestSlugAlreadyExists");

    public static final ErrorType PROBLEM_SLUGS_NOT_ALLOWED =
            ErrorType.create(ErrorType.Code.PERMISSION_DENIED, "Uriel:ContestProblemSlugsNotAllowed");

    public static final ErrorType WRONG_PROBLEM_TYPE =
            ErrorType.create(ErrorType.Code.INVALID_ARGUMENT, "Uriel:WrongProblemType");

    public static ServiceException jidAlreadyExists(String jid) {
        return new ServiceException(JID_ALREADY_EXISTS, SafeArg.of("jid", jid));
    }

    public static ServiceException slugAlreadyExists(String slug) {
        return new ServiceException(SLUG_ALREADY_EXISTS, SafeArg.of("slug", slug));
    }

    public static ServiceException problemSlugsNotAllowed(Set<String> slugs) {
        return new ServiceException(
                PROBLEM_SLUGS_NOT_ALLOWED,
                SafeArg.of("slugs", slugs.stream().collect(Collectors.joining(", "))));
    }

    public static ServiceException wrongProblemType(ProblemType problemType) {
        return new ServiceException(WRONG_PROBLEM_TYPE, SafeArg.of("problemType", problemType));
    }
}
