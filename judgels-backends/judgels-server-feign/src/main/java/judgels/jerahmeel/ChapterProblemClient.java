package judgels.jerahmeel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemData;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemWorksheet;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemsResponse;

public interface ChapterProblemClient {
    @RequestLine("GET /api/v2/chapters/{chapterJid}/problems")
    @Headers("Authorization: Bearer {token}")
    ChapterProblemsResponse getProblems(@Param("token") String token, @Param("chapterJid") String chapterJid);

    @RequestLine("PUT /api/v2/chapters/{chapterJid}/problems")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setProblems(
            @Param("token") String token,
            @Param("chapterJid") String chapterJid,
            List<ChapterProblemData> data);

    @RequestLine("GET /api/v2/chapters/{chapterJid}/problems/{problemAlias}/worksheet")
    @Headers("Authorization: Bearer {token}")
    ChapterProblemWorksheet getProblemWorksheet(
            @Param("token") String token,
            @Param("chapterJid") String chapterJid,
            @Param("problemAlias") String problemAlias);
}
