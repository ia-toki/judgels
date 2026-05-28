package tlx.archive;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/api/v2/admin/archives")
public class ArchiveAdminResource extends ArchiveResource {
    @Inject public ArchiveAdminResource() {}
}
