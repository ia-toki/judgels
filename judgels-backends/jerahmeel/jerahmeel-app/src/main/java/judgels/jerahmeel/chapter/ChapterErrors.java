package judgels.jerahmeel.chapter;

import com.palantir.conjure.java.api.errors.ErrorType;
import com.palantir.conjure.java.api.errors.ServiceException;
import com.palantir.logsafe.SafeArg;
import judgels.sandalphon.api.problem.ProblemType;

public class ChapterErrors {
    private ChapterErrors() {}

    public static final ErrorType WRONG_PROBLEM_TYPE =
            ErrorType.create(ErrorType.Code.INVALID_ARGUMENT, "Uriel:WrongProblemType");

    public static ServiceException wrongProblemType(ProblemType problemType) {
        return new ServiceException(WRONG_PROBLEM_TYPE, SafeArg.of("problemType", problemType));
    }
}
