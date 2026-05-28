package tlx.course;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/api/v2/admin/courses")
public class CourseAdminResource extends CourseResource {
    @Inject public CourseAdminResource() {}
}
