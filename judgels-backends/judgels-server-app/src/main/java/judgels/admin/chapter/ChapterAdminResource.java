package judgels.admin.chapter;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import judgels.chapter.ChapterResource;

@Path("/api/v2/admin/chapters")
public class ChapterAdminResource extends ChapterResource {
    @Inject public ChapterAdminResource() {}
}
