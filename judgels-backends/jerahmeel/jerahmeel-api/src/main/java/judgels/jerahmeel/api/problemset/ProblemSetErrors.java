package judgels.jerahmeel.api.problemset;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response.Status;
import judgels.service.api.JudgelsServiceException;

public class ProblemSetErrors {
    private ProblemSetErrors() {}

    public static final String SLUG_ALREADY_EXISTS = "Jerahmeel:ProblemSetSlugAlreadyExists";
    public static final String ARCHIVE_SLUG_NOT_FOUND = "Jerahmeel:ArchiveSlugNotFound";
    public static final String CONTEST_SLUGS_NOT_ALLOWED = "Jerahmeel:ContestSlugsNotAllowed";

    public static JudgelsServiceException slugAlreadyExists(String slug) {
        Map<String, Object> args = new HashMap<>();
        args.put("slug", slug);
        return new JudgelsServiceException(Status.BAD_REQUEST, SLUG_ALREADY_EXISTS, args);
    }

    public static JudgelsServiceException archiveSlugNotFound(String archiveSlug) {
        Map<String, Object> args = new HashMap<>();
        args.put("archiveSlug", archiveSlug);
        return new JudgelsServiceException(Status.BAD_REQUEST, ARCHIVE_SLUG_NOT_FOUND, args);
    }

    public static JudgelsServiceException contestSlugsNotAllowed(Set<String> contestSlugs) {
        Map<String, Object> args = new HashMap<>();
        args.put("contestSlugs", contestSlugs.stream().collect(Collectors.joining(", ")));
        return new JudgelsServiceException(Status.FORBIDDEN, CONTEST_SLUGS_NOT_ALLOWED, args);
    }
}
