package judgels.jerahmeel.course.chapter;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    public Set<CourseChapter> setChapters(String courseJid, List<CourseChapter> data) {
        Map<String, CourseChapter> setChapters = data.stream().collect(
                Collectors.toMap(CourseChapter::getChapterJid, Function.identity()));
        for (CourseChapterModel model : chapterDao.selectAllByCourseJid(courseJid, createOptions())) {
            CourseChapter existingChapter = setChapters.get(model.chapterJid);
            if (existingChapter == null || !existingChapter.getAlias().equals(model.alias)) {
                chapterDao.delete(model);
            }
        }

        ImmutableSet.Builder<CourseChapter> chapters = ImmutableSet.builder();
        for (CourseChapter chapter : data) {
            chapters.add(upsertChapter(
                    courseJid,
                    chapter.getAlias(),
                    chapter.getChapterJid()));
        }
        return chapters.build();
    }

    private CourseChapter upsertChapter(String courseJid, String alias, String chapterJid) {
        Optional<CourseChapterModel> maybeModel = chapterDao.selectByChapterJid(chapterJid);
        if (maybeModel.isPresent()) {
            CourseChapterModel model = maybeModel.get();
            model.alias = alias;
            return fromModel(chapterDao.update(model));
        } else {
            CourseChapterModel model = new CourseChapterModel();
            model.courseJid = courseJid;
            model.chapterJid = chapterJid;
            model.alias = alias;
            return fromModel(chapterDao.insert(model));
        }
    }

    public List<CourseChapter> getChapters(String courseJid) {
        return Lists.transform(
                chapterDao.selectAllByCourseJid(courseJid, createOptions()),
                CourseChapterStore::fromModel);
    }

    public Optional<CourseChapter> getChapterByAlias(String courseJid, String chapterAlias) {
        return chapterDao.selectByCourseJidAndChapterAlias(courseJid, chapterAlias)
                .map(CourseChapterStore::fromModel);
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
