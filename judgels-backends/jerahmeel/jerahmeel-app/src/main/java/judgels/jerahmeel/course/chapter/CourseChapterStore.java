package judgels.jerahmeel.course.chapter;

import com.google.common.collect.Lists;
import java.util.List;
import javax.inject.Inject;
import judgels.jerahmeel.api.course.chapter.CourseChapter;
import judgels.jerahmeel.persistence.CourseChapterDao;
import judgels.jerahmeel.persistence.CourseChapterModel;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;

public class CourseChapterStore {
    private final CourseChapterDao chapterDao;

    @Inject
    public CourseChapterStore(CourseChapterDao chapterDao) {
        this.chapterDao = chapterDao;
    }

    public List<CourseChapter> getChapters(String courseJid) {
        return Lists.transform(
                chapterDao.selectAllByCourseJid(courseJid, createOptions()),
                CourseChapterStore::fromModel);
    }

    private static SelectionOptions createOptions() {
        return new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_ALL)
                .orderBy("alias")
                .orderDir(OrderDir.ASC)
                .build();
    }

    private static CourseChapter fromModel(CourseChapterModel model) {
        return new CourseChapter.Builder()
                .chapterJid(model.chapterJid)
                .alias(model.alias)
                .build();
    }
}
