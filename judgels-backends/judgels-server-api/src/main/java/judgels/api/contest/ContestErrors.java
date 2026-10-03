package judgels.api.contest;

import jakarta.ws.rs.core.Response.Status;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.api.problem.ProblemType;
import judgels.core.api.JudgelsApiException;

public class ContestErrors {
    private ContestErrors() {}

    public static final String JID_ALREADY_EXISTS = "ContestJidAlreadyExists";
    public static final String SLUG_ALREADY_EXISTS = "ContestSlugAlreadyExists";
    public static final String PROBLEM_SLUGS_NOT_ALLOWED = "ContestProblemSlugsNotAllowed";
    public static final String WRONG_PROBLEM_TYPE = "WrongProblemType";
    public static final String CLARIFICATION_ALREADY_ANSWERED = "ClarificationAlreadyAnswered";

    public static JudgelsApiException jidAlreadyExists(String jid) {
        Map<String, Object> args = new HashMap<>();
        args.put("jid", jid);
        return new JudgelsApiException(Status.BAD_REQUEST, JID_ALREADY_EXISTS, args);
    }

    public static JudgelsApiException slugAlreadyExists(String slug) {
        Map<String, Object> args = new HashMap<>();
        args.put("slug", slug);
        return new JudgelsApiException(Status.BAD_REQUEST, SLUG_ALREADY_EXISTS, args);
    }

    public static JudgelsApiException problemSlugsNotAllowed(Set<String> slugs) {
        Map<String, Object> args = new HashMap<>();
        args.put("slugs", slugs.stream().collect(Collectors.joining(", ")));
        return new JudgelsApiException(Status.FORBIDDEN, PROBLEM_SLUGS_NOT_ALLOWED, args);
    }

    public static JudgelsApiException wrongProblemType(ProblemType problemType) {
        Map<String, Object> args = new HashMap<>();
        args.put("problemType", problemType);
        return new JudgelsApiException(Status.BAD_REQUEST, WRONG_PROBLEM_TYPE, args);
    }

    public static JudgelsApiException clarificationAlreadyAnswered(String clarificationJid) {
        Map<String, Object> args = new HashMap<>();
        args.put("clarificationJid", clarificationJid);
        return new JudgelsApiException(Status.BAD_REQUEST, CLARIFICATION_ALREADY_ANSWERED, args);
    }
}
