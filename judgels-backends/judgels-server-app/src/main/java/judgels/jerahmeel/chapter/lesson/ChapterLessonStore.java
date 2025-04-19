package judgels.jerahmeel.chapter.lesson;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import judgels.jerahmeel.api.chapter.lesson.ChapterLesson;
import judgels.jerahmeel.persistence.ChapterLessonDao;
import judgels.jerahmeel.persistence.ChapterLessonModel;
import judgels.jerahmeel.persistence.ChapterLessonModel_;
import judgels.persistence.api.OrderDir;

public class ChapterLessonStore {
    private final ChapterLessonDao lessonDao;

    @Inject
    public ChapterLessonStore(ChapterLessonDao lessonDao) {
        this.lessonDao = lessonDao;
    }

    public List<ChapterLesson> getLessons(String chapterJid) {
        return Lists.transform(lessonDao
                .selectByChapterJid(chapterJid)
                .orderBy(ChapterLessonModel_.ALIAS, OrderDir.ASC)
                .all(), ChapterLessonStore::fromModel);
    }

    public Optional<ChapterLesson> getLessonByAlias(String chapterJid, String lessonAlias) {
        return lessonDao.selectByChapterJidAndLessonAlias(chapterJid, lessonAlias)
                .map(ChapterLessonStore::fromModel);
    }

    public Set<ChapterLesson> setLessons(String chapterJid, List<ChapterLesson> data) {
        Map<String, ChapterLesson> setLessons = data.stream().collect(
                Collectors.toMap(ChapterLesson::getLessonJid, Function.identity()));
        for (ChapterLessonModel model : lessonDao.selectByChapterJid(chapterJid).all()) {
            ChapterLesson existingLesson = setLessons.get(model.lessonJid);
            if (existingLesson == null || !existingLesson.getAlias().equals(model.alias)) {
                lessonDao.delete(model);
            }
        }

        ImmutableSet.Builder<ChapterLesson> lessons = ImmutableSet.builder();
        for (ChapterLesson lesson : data) {
            lessons.add(upsertLesson(
                    chapterJid,
                    lesson.getAlias(),
                    lesson.getLessonJid()));
        }
        return lessons.build();
    }

    public ChapterLesson upsertLesson(String chapterJid, String alias, String lessonJid) {
        Optional<ChapterLessonModel> maybeModel = lessonDao.selectByLessonJid(lessonJid);
        if (maybeModel.isPresent()) {
            ChapterLessonModel model = maybeModel.get();
            model.alias = alias;
            return fromModel(lessonDao.update(model));
        } else {
            ChapterLessonModel model = new ChapterLessonModel();
            model.chapterJid = chapterJid;
            model.alias = alias;
            model.lessonJid = lessonJid;
            return fromModel(lessonDao.insert(model));
        }
    }

    private static ChapterLesson fromModel(ChapterLessonModel model) {
        return new ChapterLesson.Builder()
                .lessonJid(model.lessonJid)
                .alias(model.alias)
                .build();
    }
}
