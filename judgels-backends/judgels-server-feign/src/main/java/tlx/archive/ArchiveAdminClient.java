package tlx.archive;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import tlx.api.archive.Archive;
import tlx.api.archive.ArchiveCreateData;
import tlx.api.archive.ArchiveUpdateData;

public interface ArchiveAdminClient {
    @RequestLine("POST /api/v2/admin/archives")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Archive createArchive(@Param("token") String token, ArchiveCreateData data);

    @RequestLine("POST /api/v2/admin/archives/{archiveJid}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Archive updateArchive(
            @Param("token") String token,
            @Param("archiveJid") String archiveJid,
            ArchiveUpdateData data);
}
