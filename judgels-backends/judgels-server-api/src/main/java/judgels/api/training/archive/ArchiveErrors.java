package judgels.api.training.archive;

import jakarta.ws.rs.core.Response.Status;
import java.util.HashMap;
import java.util.Map;
import judgels.core.api.JudgelsApiException;

public class ArchiveErrors {
    private ArchiveErrors() {}

    public static final String SLUG_ALREADY_EXISTS = "ArchiveSlugAlreadyExists";

    public static JudgelsApiException slugAlreadyExists(String slug) {
        Map<String, Object> args = new HashMap<>();
        args.put("slug", slug);
        return new JudgelsApiException(Status.BAD_REQUEST, SLUG_ALREADY_EXISTS, args);
    }
}
