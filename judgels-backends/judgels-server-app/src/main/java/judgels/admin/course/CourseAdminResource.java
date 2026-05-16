package judgels.admin.course;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import judgels.course.CourseResource;

@Path("/api/v2/admin/courses")
public class CourseAdminResource extends CourseResource {
    @Inject public CourseAdminResource() {}
}
