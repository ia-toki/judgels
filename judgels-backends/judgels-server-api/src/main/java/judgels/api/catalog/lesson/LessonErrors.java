package judgels.api.catalog.lesson;

import jakarta.ws.rs.core.Response.Status;
import java.util.HashMap;
import java.util.Map;
import judgels.core.api.JudgelsApiException;

public class LessonErrors {
    private LessonErrors() {}

    public static final String SLUG_ALREADY_EXISTS = "LessonSlugAlreadyExists";
    public static final String VERSION_LOCAL_CHANGES_OUTDATED = "LessonVersionLocalChangesOutdated";
    public static final String VERSION_LOCAL_CHANGES_CONFLICT = "LessonVersionLocalChangesConflict";

    public static JudgelsApiException slugAlreadyExists(String slug) {
        Map<String, Object> args = new HashMap<>();
        args.put("slug", slug);
        return new JudgelsApiException(Status.BAD_REQUEST, SLUG_ALREADY_EXISTS, args);
    }

    public static JudgelsApiException versionLocalChangesOutdated() {
        return new JudgelsApiException(Status.BAD_REQUEST, VERSION_LOCAL_CHANGES_OUTDATED);
    }

    public static JudgelsApiException versionLocalChangesConflict() {
        return new JudgelsApiException(Status.BAD_REQUEST, VERSION_LOCAL_CHANGES_CONFLICT);
    }
}
