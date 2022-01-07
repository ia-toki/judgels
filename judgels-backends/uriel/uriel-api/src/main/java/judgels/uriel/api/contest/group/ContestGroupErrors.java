package judgels.uriel.api.contest.group;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import judgels.service.api.JudgelsServiceException;

public class ContestGroupErrors {
    private ContestGroupErrors() {}

    public static final String SLUG_ALREADY_EXISTS = "Uriel:ContestGroupSlugAlreadyExists";

    public static JudgelsServiceException slugAlreadyExists(String slug) {
        Map<String, Object> args = new HashMap<>();
        args.put("slug", slug);
        return new JudgelsServiceException(Response.Status.BAD_REQUEST, SLUG_ALREADY_EXISTS, args);
    }
}
