package tlx.admin.chapter;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import tlx.chapter.ChapterResource;

@Path("/api/v2/admin/chapters")
public class ChapterAdminResource extends ChapterResource {
    @Inject public ChapterAdminResource() {}
}
