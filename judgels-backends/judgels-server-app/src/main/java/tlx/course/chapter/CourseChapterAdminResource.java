package tlx.course.chapter;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/api/v2/admin/courses/{courseJid}/chapters")
public class CourseChapterAdminResource extends CourseChapterResource {
    @Inject public CourseChapterAdminResource() {}
}
