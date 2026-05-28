package tlx.chapter;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;
import tlx.api.chapter.problem.ChapterProblemData;
import tlx.api.chapter.problem.ChapterProblemWorksheet;
import tlx.api.chapter.problem.ChapterProblemsResponse;

public interface ChapterProblemClient {
    @RequestLine("GET /api/v2/chapters/{chapterJid}/problems")
    @Headers("Authorization: Bearer {token}")
    ChapterProblemsResponse getProblems(@Param("token") String token, @Param("chapterJid") String chapterJid);

    @RequestLine("GET /api/v2/chapters/{chapterJid}/problems/{problemAlias}/worksheet")
    @Headers("Authorization: Bearer {token}")
    ChapterProblemWorksheet getProblemWorksheet(
            @Param("token") String token,
            @Param("chapterJid") String chapterJid,
            @Param("problemAlias") String problemAlias);

    @RequestLine("PUT /api/v2/chapters/{chapterJid}/problems")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setProblems(
            @Param("token") String token,
            @Param("chapterJid") String chapterJid,
            List<ChapterProblemData> data);
}
