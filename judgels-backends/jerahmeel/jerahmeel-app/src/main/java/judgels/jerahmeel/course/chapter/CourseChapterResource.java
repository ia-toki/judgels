package judgels.jerahmeel.course.chapter;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterInfo;
import judgels.jerahmeel.api.chapter.ChapterProgress;
import judgels.jerahmeel.api.chapter.lesson.ChapterLesson;
import judgels.jerahmeel.api.course.chapter.CourseChapter;
import judgels.jerahmeel.api.course.chapter.CourseChapterResponse;
import judgels.jerahmeel.api.course.chapter.CourseChapterService;
import judgels.jerahmeel.api.course.chapter.CourseChapterUserProgressesData;
import judgels.jerahmeel.api.course.chapter.CourseChapterUserProgressesResponse;
import judgels.jerahmeel.api.course.chapter.CourseChaptersResponse;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.jerahmeel.chapter.lesson.ChapterLessonStore;
import judgels.jerahmeel.course.CourseStore;
import judgels.jerahmeel.role.RoleChecker;
import judgels.jerahmeel.stats.StatsStore;
import judgels.jophiel.user.UserClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class CourseChapterResource implements CourseChapterService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final CourseStore courseStore;
    private final CourseChapterStore courseChapterStore;
    private final ChapterStore chapterStore;
    private final ChapterLessonStore chapterLessonStore;
    private final StatsStore statsStore;
    private final UserClient userClient;

    @Inject
    public CourseChapterResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            CourseStore courseStore,
            CourseChapterStore courseChapterStore,
            ChapterStore chapterStore,
            ChapterLessonStore chapterLessonStore,
            StatsStore statsStore,
            UserClient userClient) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.courseStore = courseStore;
        this.courseChapterStore = courseChapterStore;
        this.chapterLessonStore = chapterLessonStore;
        this.chapterStore = chapterStore;
        this.statsStore = statsStore;
        this.userClient = userClient;
    }

    @Override
    @UnitOfWork
    public void setChapters(AuthHeader authHeader, String courseJid, List<CourseChapter> data) {
        String actorJid = actorChecker.check(authHeader);
        checkFound(courseStore.getCourseByJid(courseJid));
        checkAllowed(roleChecker.isAdmin(actorJid));

        Set<String> aliases = data.stream().map(CourseChapter::getAlias).collect(toSet());
        Set<String> chapterJids = data.stream().map(CourseChapter::getChapterJid).collect(toSet());

        checkArgument(aliases.size() == data.size(), "Chapter aliases must be unique");
        checkArgument(chapterJids.size() == data.size(), "Chapter JIDs must be unique");

        courseChapterStore.setChapters(courseJid, data);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public CourseChaptersResponse getChapters(Optional<AuthHeader> authHeader, String courseJid) {
        String actorJid = actorChecker.check(authHeader);
        checkFound(courseStore.getCourseByJid(courseJid));

        List<CourseChapter> chapters = courseChapterStore.getChapters(courseJid);
        Set<String> chapterJids = chapters.stream().map(CourseChapter::getChapterJid).collect(toSet());
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
    public CourseChapterResponse getChapter(Optional<AuthHeader> authHeader, String courseJid, String chapterAlias) {
        checkFound(courseStore.getCourseByJid(courseJid));

        CourseChapter courseChapter = checkFound(courseChapterStore.getChapterByAlias(courseJid, chapterAlias));
        Chapter chapter = checkFound(chapterStore.getChapterByJid(courseChapter.getChapterJid()));
        List<String> lessonAliases = Lists.transform(
                chapterLessonStore.getLessons(chapter.getJid()),
                ChapterLesson::getAlias);
        return new CourseChapterResponse.Builder()
                .jid(chapter.getJid())
                .name(chapter.getName())
                .lessonAliases(lessonAliases)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public CourseChapterUserProgressesResponse getChapterUserProgresses(
            Optional<AuthHeader> authHeader,
            String courseJid,
            CourseChapterUserProgressesData data) {

        checkFound(courseStore.getCourseByJid(courseJid));

        checkArgument(data.getUsernames().size() <= 100, "Cannot get more than 100 users.");

        List<CourseChapter> chapters = courseChapterStore.getChapters(courseJid);
        Set<String> chapterJids = chapters.stream().map(CourseChapter::getChapterJid).collect(toSet());

        Map<String, Integer> totalProblemsMap = statsStore.getChapterTotalProblemsMap(chapterJids);
        List<Integer> totalProblemsList = chapters.stream()
                .map(CourseChapter::getChapterJid)
                .map(totalProblemsMap::get)
                .collect(toList());

        Map<String, String> usernameToJidsMap =
                userClient.translateUsernamesToJids(ImmutableSet.copyOf(data.getUsernames()));
        Map<String, Map<String, Integer>> userSolvedProblemsMap = statsStore.getUserChapterSolvedProblemsMap(
                    ImmutableSet.copyOf(usernameToJidsMap.values()),
                    chapterJids);

        Map<String, List<Integer>> userProgressesMap = new LinkedHashMap<>();
        for (String username : data.getUsernames()) {
            if (!usernameToJidsMap.containsKey(username)) {
                continue;
            }
            String userJid = usernameToJidsMap.get(username);
            userProgressesMap.put(username, chapters.stream()
                    .map(CourseChapter::getChapterJid)
                    .map(chapterJid -> userSolvedProblemsMap.get(userJid).get(chapterJid))
                    .collect(toList()));
        }

        return new CourseChapterUserProgressesResponse.Builder()
                .totalProblemsList(totalProblemsList)
                .userProgressesMap(userProgressesMap)
                .build();
    }
}
