package judgels.jerahmeel.chapter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterCreateData;
import judgels.jerahmeel.api.chapter.ChapterInfo;
import judgels.jerahmeel.api.chapter.ChapterUpdateData;
import judgels.jerahmeel.persistence.ChapterDao;
import judgels.jerahmeel.persistence.ChapterModel;
import judgels.jerahmeel.persistence.CourseChapterDao;
import judgels.jerahmeel.persistence.CourseChapterModel;
import judgels.jerahmeel.persistence.CourseDao;
import judgels.jerahmeel.persistence.CourseModel;

public class ChapterStore {
    private final ChapterDao chapterDao;
    private final CourseChapterDao courseChapterDao;
    private final CourseDao courseDao;

    @Inject
    public ChapterStore(ChapterDao chapterDao, CourseChapterDao courseChapterDao, CourseDao courseDao) {
        this.chapterDao = chapterDao;
        this.courseChapterDao = courseChapterDao;
        this.courseDao = courseDao;
    }

    public List<Chapter> getChapters() {
        return Lists.transform(chapterDao.select().all(), ChapterStore::fromModel);
    }

    public Optional<Chapter> getChapterByJid(String chapterJid) {
        return chapterDao.selectByJid(chapterJid).map(ChapterStore::fromModel);
    }

    public Map<String, ChapterInfo> getChapterInfosByJids(Collection<String> chapterJids) {
        return chapterDao.selectByJids(chapterJids)
                .values()
                .stream()
                .collect(Collectors.toMap(
                        c -> c.jid,
                        c -> new ChapterInfo.Builder()
                                .name(c.name)
                                .build()));
    }

    public Map<String, String> getChapterNamesByJids(Collection<String> chapterJids) {
        return chapterDao.selectByJids(chapterJids)
                .values()
                .stream()
                .collect(Collectors.toMap(
                        c -> c.jid,
                        c -> c.name));
    }

    public Map<String, List<String>> getChapterPathsByJids(Collection<String> chapterJids) {
        List<CourseChapterModel> chapters = courseChapterDao.selectAllByChapterJids(chapterJids);
        Map<String, CourseChapterModel> chapterToCourseChapterMap = new HashMap<>();
        chapters.forEach(c -> chapterToCourseChapterMap.put(c.chapterJid, c));

        var courseJids = Lists.transform(chapters, c -> c.courseJid);
        Map<String, CourseModel> coursesMap = courseDao.selectByJids(courseJids);

        Map<String, List<String>> pathsMap = new HashMap<>();
        for (String chapterJid : chapterToCourseChapterMap.keySet()) {
            CourseChapterModel m = chapterToCourseChapterMap.get(chapterJid);
            CourseModel cm = coursesMap.get(m.courseJid);
            if (cm != null) {
                pathsMap.put(chapterJid, ImmutableList.of(cm.slug, m.alias));
            }
        }
        return Map.copyOf(pathsMap);
    }

    public Optional<List<String>> getChapterPathByJid(String chapterJid) {
        Map<String, List<String>> pathsMap = getChapterPathsByJids(ImmutableSet.of(chapterJid));
        return Optional.ofNullable(pathsMap.get(chapterJid));
    }

    public Chapter createChapter(ChapterCreateData data) {
        ChapterModel model = new ChapterModel();
        model.name = data.getName();
        model.description = "";
        return fromModel(chapterDao.insert(model));
    }

    public Chapter updateChapter(String chapterJid, ChapterUpdateData data) {
        ChapterModel model = chapterDao.findByJid(chapterJid);
        data.getName().ifPresent(name -> model.name = name);
        return fromModel(chapterDao.update(model));
    }

    private static Chapter fromModel(ChapterModel model) {
        return new Chapter.Builder()
                .id(model.id)
                .jid(model.jid)
                .name(model.name)
                .description(model.description)
                .build();
    }
}
