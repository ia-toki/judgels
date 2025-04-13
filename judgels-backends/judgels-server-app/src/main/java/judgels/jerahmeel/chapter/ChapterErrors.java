package judgels.jerahmeel.chapter;

import jakarta.ws.rs.core.Response.Status;
import java.util.HashMap;
import java.util.Map;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.service.api.JudgelsServiceException;

public class ChapterErrors {
    private ChapterErrors() {}

    public static final String WRONG_PROBLEM_TYPE = "Jerahmeel:WrongProblemType";

    public static JudgelsServiceException wrongProblemType(ProblemType problemType) {
        Map<String, Object> args = new HashMap<>();
        args.put("problemType", problemType);
        return new JudgelsServiceException(Status.BAD_REQUEST, WRONG_PROBLEM_TYPE, args);
    }
}
