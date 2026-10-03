package judgels.training.chapter;

import jakarta.ws.rs.core.Response.Status;
import java.util.HashMap;
import java.util.Map;
import judgels.api.problem.ProblemType;
import judgels.core.api.JudgelsApiException;

public class ChapterErrors {
    private ChapterErrors() {}

    public static final String WRONG_PROBLEM_TYPE = "WrongProblemType";

    public static JudgelsApiException wrongProblemType(ProblemType problemType) {
        Map<String, Object> args = new HashMap<>();
        args.put("problemType", problemType);
        return new JudgelsApiException(Status.BAD_REQUEST, WRONG_PROBLEM_TYPE, args);
    }
}
