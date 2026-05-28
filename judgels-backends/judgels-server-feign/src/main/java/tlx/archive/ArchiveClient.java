package tlx.archive;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import tlx.api.archive.Archive;
import tlx.api.archive.ArchiveCreateData;
import tlx.api.archive.ArchiveUpdateData;
import tlx.api.archive.ArchivesResponse;

public interface ArchiveClient {
    @RequestLine("GET /api/v2/archives")
    @Headers("Authorization: Bearer {token}")
    ArchivesResponse getArchives(@Param("token") String token);

    @RequestLine("POST /api/v2/archives")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Archive createArchive(@Param("token") String token, ArchiveCreateData data);

    @RequestLine("POST /api/v2/archives/{archiveJid}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Archive updateArchive(
            @Param("token") String token,
            @Param("archiveJid") String archiveJid,
            ArchiveUpdateData data);
}
