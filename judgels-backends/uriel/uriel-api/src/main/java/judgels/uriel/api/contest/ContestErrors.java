package judgels.uriel.api.contest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response.Status;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.service.api.JudgelsServiceException;

public class ContestErrors {
    private ContestErrors() {}

    public static final String JID_ALREADY_EXISTS = "Uriel:ContestJidAlreadyExists";
    public static final String SLUG_ALREADY_EXISTS = "Uriel:ContestSlugAlreadyExists";
    public static final String PROBLEM_SLUGS_NOT_ALLOWED = "Uriel:ContestProblemSlugsNotAllowed";
    public static final String WRONG_PROBLEM_TYPE = "Uriel:WrongProblemType";
    public static final String CLARIFICATION_ALREADY_ANSWERED = "Uriel:ClarificationAlreadyAnswered";

    public static JudgelsServiceException jidAlreadyExists(String jid) {
        Map<String, Object> args = new HashMap<>();
        args.put("jid", jid);
        return new JudgelsServiceException(Status.BAD_REQUEST, JID_ALREADY_EXISTS, args);
    }

    public static JudgelsServiceException slugAlreadyExists(String slug) {
        Map<String, Object> args = new HashMap<>();
        args.put("slug", slug);
        return new JudgelsServiceException(Status.BAD_REQUEST, SLUG_ALREADY_EXISTS, args);
    }

    public static JudgelsServiceException problemSlugsNotAllowed(Set<String> slugs) {
        Map<String, Object> args = new HashMap<>();
        args.put("slugs", slugs.stream().collect(Collectors.joining(", ")));
        return new JudgelsServiceException(Status.FORBIDDEN, PROBLEM_SLUGS_NOT_ALLOWED, args);
    }

    public static JudgelsServiceException wrongProblemType(ProblemType problemType) {
        Map<String, Object> args = new HashMap<>();
        args.put("problemType", problemType);
        return new JudgelsServiceException(Status.BAD_REQUEST, WRONG_PROBLEM_TYPE, args);
    }

    public static JudgelsServiceException clarificationAlreadyAnswered(String clarificationJid) {
        Map<String, Object> args = new HashMap<>();
        args.put("clarificationJid", clarificationJid);
        return new JudgelsServiceException(Status.BAD_REQUEST, CLARIFICATION_ALREADY_ANSWERED, args);
    }
}
