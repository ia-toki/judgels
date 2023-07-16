package judgels.uriel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.form.FormData;
import judgels.uriel.api.contest.file.ContestFilesResponse;

public interface ContestFileClient {
    @RequestLine("GET /api/v2/contests/{contestJid}/files")
    @Headers("Authorization: Bearer {token}")
    ContestFilesResponse getFiles(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("POST /api/v2/contests/{contestJid}/files")
    @Headers({"Authorization: Bearer {token}", "Content-Type: multipart/form-data"})
    void uploadFile(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @Param("file") FormData file);

    @RequestLine("GET /api/v2/contests/{contestJid}/files/{filename}")
    ContestFilesResponse downloadFile(@Param("contestJid") String contestJid, @Param("filename") String filename);
}
