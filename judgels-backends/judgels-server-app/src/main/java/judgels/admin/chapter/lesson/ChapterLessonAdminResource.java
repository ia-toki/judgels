package judgels.admin.chapter.lesson;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import judgels.chapter.lesson.ChapterLessonResource;

@Path("/api/v2/admin/chapters/{chapterJid}/lessons")
public class ChapterLessonAdminResource extends ChapterLessonResource {
    @Inject public ChapterLessonAdminResource() {}
}
