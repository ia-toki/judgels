package tlx.chapter;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;
import tlx.api.chapter.problem.ChapterProblemData;

public interface ChapterProblemAdminClient {
    @RequestLine("PUT /api/v2/admin/chapters/{chapterJid}/problems")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setProblems(
            @Param("token") String token,
            @Param("chapterJid") String chapterJid,
            List<ChapterProblemData> data);
}
