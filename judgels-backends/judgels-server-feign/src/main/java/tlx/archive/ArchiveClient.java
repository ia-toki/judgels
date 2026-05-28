package tlx.archive;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import tlx.api.archive.ArchivesResponse;

public interface ArchiveClient {
    @RequestLine("GET /api/v2/archives")
    @Headers("Authorization: Bearer {token}")
    ArchivesResponse getArchives(@Param("token") String token);
}
