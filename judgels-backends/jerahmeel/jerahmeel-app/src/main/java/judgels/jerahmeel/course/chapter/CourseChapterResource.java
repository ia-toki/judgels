package judgels.jerahmeel.course.chapter;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterInfo;
import judgels.jerahmeel.api.chapter.ChapterProgress;
import judgels.jerahmeel.api.course.chapter.CourseChapter;
import judgels.jerahmeel.api.course.chapter.CourseChapterService;
import judgels.jerahmeel.api.course.chapter.CourseChaptersResponse;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.jerahmeel.course.CourseStore;
import judgels.jerahmeel.stats.StatsStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class CourseChapterResource implements CourseChapterService {
    private final ActorChecker actorChecker;
    private final CourseStore courseStore;
    private final CourseChapterStore courseChapterStore;
    private final ChapterStore chapterStore;
    private final StatsStore statsStore;

    @Inject
    public CourseChapterResource(
            ActorChecker actorChecker,
            CourseStore courseStore,
            CourseChapterStore courseChapterStore,
            ChapterStore chapterStore,
            StatsStore statsStore) {

        this.actorChecker = actorChecker;
        this.courseStore = courseStore;
        this.courseChapterStore = courseChapterStore;
        this.chapterStore = chapterStore;
        this.statsStore = statsStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public CourseChaptersResponse getChapters(Optional<AuthHeader> authHeader, String courseJid) {
        String actorJid = actorChecker.check(authHeader);
        checkFound(courseStore.getCourseByJid(courseJid));

        List<CourseChapter> chapters = courseChapterStore.getChapters(courseJid);
        Set<String> chapterJids = chapters.stream().map(CourseChapter::getChapterJid).collect(Collectors.toSet());
        Map<String, ChapterInfo> chaptersMap = chapterStore.getChapterInfosByJids(chapterJids);
        Map<String, ChapterProgress> chapterProgressesMap = statsStore.getChapterProgressesMap(actorJid, chapterJids);

        return new CourseChaptersResponse.Builder()
                .data(chapters)
                .chaptersMap(chaptersMap)
                .chapterProgressesMap(chapterProgressesMap)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Chapter getChapter(Optional<AuthHeader> authHeader, String courseJid, String chapterAlias) {
        String actorJid = actorChecker.check(authHeader);
        checkFound(courseStore.getCourseByJid(courseJid));

        CourseChapter courseChapter = checkFound(courseChapterStore.getChapterByAlias(courseJid, chapterAlias));
        return checkFound(chapterStore.getChapterByJid(courseChapter.getChapterJid()));
    }
}
