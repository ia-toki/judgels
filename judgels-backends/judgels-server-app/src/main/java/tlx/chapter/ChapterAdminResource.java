package tlx.chapter;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/api/v2/admin/chapters")
public class ChapterAdminResource extends ChapterResource {
    @Inject public ChapterAdminResource() {}
}
