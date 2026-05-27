package tlx.admin.chapter.problem;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import tlx.chapter.problem.ChapterProblemResource;

@Path("/api/v2/admin/chapters/{chapterJid}/problems")
public class ChapterProblemAdminResource extends ChapterProblemResource {
    @Inject public ChapterProblemAdminResource() {}
}
