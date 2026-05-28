package tlx.chapter.lesson;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/api/v2/admin/chapters/{chapterJid}/lessons")
public class ChapterLessonAdminResource extends ChapterLessonResource {
    @Inject public ChapterLessonAdminResource() {}
}
