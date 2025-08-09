package judgels.jerahmeel.api.course;

import jakarta.ws.rs.core.Response.Status;
import java.util.HashMap;
import java.util.Map;
import judgels.service.api.JudgelsServiceException;

public class CourseErrors {
    private CourseErrors() {}

    public static final String SLUG_ALREADY_EXISTS = "Jerahmeel:CourseSlugAlreadyExists";

    public static JudgelsServiceException slugAlreadyExists(String slug) {
        Map<String, Object> args = new HashMap<>();
        args.put("slug", slug);
        return new JudgelsServiceException(Status.BAD_REQUEST, SLUG_ALREADY_EXISTS, args);
    }
}
