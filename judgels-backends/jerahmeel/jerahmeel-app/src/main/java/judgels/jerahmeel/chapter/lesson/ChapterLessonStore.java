package judgels.jerahmeel.chapter.lesson;

import com.google.common.collect.Lists;
import java.util.List;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.lesson.ChapterLesson;
import judgels.jerahmeel.persistence.ChapterLessonDao;
import judgels.jerahmeel.persistence.ChapterLessonModel;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;

public class ChapterLessonStore {
    private final ChapterLessonDao chapterDao;

    @Inject
    public ChapterLessonStore(ChapterLessonDao chapterDao) {
        this.chapterDao = chapterDao;
    }

    public List<ChapterLesson> getLessons(String chapterJid) {
        return Lists.transform(
                chapterDao.selectAllByChapterJid(chapterJid, createOptions()),
                ChapterLessonStore::fromModel);
    }

    private static SelectionOptions createOptions() {
        return new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_ALL)
                .orderBy("alias")
                .orderDir(OrderDir.ASC)
                .build();
    }

    private static ChapterLesson fromModel(ChapterLessonModel model) {
        return new ChapterLesson.Builder()
                .lessonJid(model.lessonJid)
                .alias(model.alias)
                .build();
    }
}
