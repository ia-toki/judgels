package judgels.admin.archive;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import judgels.archive.ArchiveResource;

@Path("/api/v2/admin/archives")
public class ArchiveAdminResource extends ArchiveResource {
    @Inject public ArchiveAdminResource() {}
}
