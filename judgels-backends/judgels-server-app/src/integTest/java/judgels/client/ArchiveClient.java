package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.training.archive.Archive;
import judgels.api.training.archive.ArchiveCreateData;
import judgels.api.training.archive.ArchiveUpdateData;
import judgels.api.training.archive.ArchivesResponse;

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
