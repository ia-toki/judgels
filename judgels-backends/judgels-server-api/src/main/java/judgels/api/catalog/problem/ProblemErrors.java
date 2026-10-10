package judgels.api.catalog.problem;

import jakarta.ws.rs.core.Response.Status;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import judgels.core.api.JudgelsApiException;

public class ProblemErrors {
    private ProblemErrors() {}

    public static final String SLUG_ALREADY_EXISTS = "ProblemSlugAlreadyExists";
    public static final String SETTER_USERNAMES_NOT_FOUND = "ProblemSetterUsernamesNotFound";
    public static final String VERSION_LOCAL_CHANGES_OUTDATED = "ProblemVersionLocalChangesOutdated";
    public static final String VERSION_LOCAL_CHANGES_CONFLICT = "ProblemVersionLocalChangesConflict";

    public static JudgelsApiException slugAlreadyExists(String slug) {
        Map<String, Object> args = new HashMap<>();
        args.put("slug", slug);
        return new JudgelsApiException(Status.BAD_REQUEST, SLUG_ALREADY_EXISTS, args);
    }

    public static JudgelsApiException setterUsernamesNotFound(Collection<String> usernames) {
        Map<String, Object> args = new HashMap<>();
        args.put("usernames", String.join(", ", usernames));
        return new JudgelsApiException(Status.BAD_REQUEST, SETTER_USERNAMES_NOT_FOUND, args);
    }

    public static JudgelsApiException versionLocalChangesOutdated() {
        return new JudgelsApiException(Status.BAD_REQUEST, VERSION_LOCAL_CHANGES_OUTDATED);
    }

    public static JudgelsApiException versionLocalChangesConflict() {
        return new JudgelsApiException(Status.BAD_REQUEST, VERSION_LOCAL_CHANGES_CONFLICT);
    }
}
