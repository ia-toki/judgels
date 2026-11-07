package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;
import judgels.api.catalog.problem.writer.ProblemWriterData;
import judgels.api.catalog.problem.writer.ProblemWritersResponse;

public interface ProblemWriterClient {
    @RequestLine("GET /api/v4/problems/writers")
    @Headers("Authorization: Bearer {token}")
    ProblemWritersResponse getWriters(@Param("token") String token);

    @RequestLine("PUT /api/v4/problems/writers")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setWriters(@Param("token") String token, List<ProblemWriterData> data);
}
