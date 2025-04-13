package judgels.jerahmeel.course.chapter;

import static com.google.common.base.Preconditions.checkArgument;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterInfo;
import judgels.jerahmeel.api.chapter.ChapterProgress;
import judgels.jerahmeel.api.chapter.lesson.ChapterLesson;
import judgels.jerahmeel.api.course.chapter.CourseChapter;
import judgels.jerahmeel.api.course.chapter.CourseChapterResponse;
import judgels.jerahmeel.api.course.chapter.CourseChapterUserProgressesData;
import judgels.jerahmeel.api.course.chapter.CourseChapterUserProgressesResponse;
import judgels.jerahmeel.api.course.chapter.CourseChaptersResponse;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.jerahmeel.chapter.lesson.ChapterLessonStore;
import judgels.jerahmeel.course.CourseStore;
import judgels.jerahmeel.role.RoleChecker;
import judgels.jerahmeel.stats.StatsStore;
import judgels.jophiel.JophielClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/courses/{courseJid}/chapters")
public class CourseChapterResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected RoleChecker roleChecker;
    @Inject protected CourseStore courseStore;
    @Inject protected CourseChapterStore courseChapterStore;
    @Inject protected ChapterStore chapterStore;
    @Inject protected ChapterLessonStore chapterLessonStore;
    @Inject protected StatsStore statsStore;
    @Inject protected JophielClient jophielClient;

    @Inject public CourseChapterResource() {}

    @PUT
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void setChapters(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("courseJid") String courseJid,
            List<CourseChapter> data) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(courseStore.getCourseByJid(courseJid));
        checkAllowed(roleChecker.isAdmin(actorJid));

        Set<String> aliases = data.stream().map(CourseChapter::getAlias).collect(toSet());
        Set<String> chapterJids = data.stream().map(CourseChapter::getChapterJid).collect(toSet());

        checkArgument(aliases.size() == data.size(), "Chapter aliases must be unique");
        checkArgument(chapterJids.size() == data.size(), "Chapter JIDs must be unique");

        courseChapterStore.setChapters(courseJid, data);
    }

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public CourseChaptersResponse getChapters(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("courseJid") String courseJid) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(courseStore.getCourseByJid(courseJid));

        List<CourseChapter> chapters = courseChapterStore.getChapters(courseJid);

        var chapterJids = Lists.transform(chapters, CourseChapter::getChapterJid);
        Map<String, ChapterInfo> chaptersMap = chapterStore.getChapterInfosByJids(chapterJids);
        Map<String, ChapterProgress> chapterProgressesMap = statsStore.getChapterProgressesMap(actorJid, chapterJids);

        return new CourseChaptersResponse.Builder()
                .data(chapters)
                .chaptersMap(chaptersMap)
                .chapterProgressesMap(chapterProgressesMap)
                .build();
    }

    @GET
    @Path("/{chapterAlias}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public CourseChapterResponse getChapter(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("courseJid") String courseJid,
            @PathParam("chapterAlias") String chapterAlias) {

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

    @POST
    @Path("/user-progresses")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public CourseChapterUserProgressesResponse getChapterUserProgresses(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("courseJid") String courseJid,
            CourseChapterUserProgressesData data) {

        checkFound(courseStore.getCourseByJid(courseJid));

        checkArgument(data.getUsernames().size() <= 100, "Cannot get more than 100 users.");

        List<CourseChapter> chapters = courseChapterStore.getChapters(courseJid);

        var chapterJids = Lists.transform(chapters, CourseChapter::getChapterJid);
        Map<String, Integer> totalProblemsMap = statsStore.getChapterTotalProblemsMap(chapterJids);
        List<Integer> totalProblemsList = chapters.stream()
                .map(CourseChapter::getChapterJid)
                .map(totalProblemsMap::get)
                .collect(toList());

        Map<String, String> usernameToJidsMap = jophielClient.translateUsernamesToJids(data.getUsernames());
        Map<String, Map<String, Integer>> userSolvedProblemsMap = statsStore.getUserChapterSolvedProblemsMap(
                    usernameToJidsMap.values(),
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
