package judgels.jerahmeel.chapter.lesson;

import static com.google.common.base.Preconditions.checkArgument;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.jerahmeel.api.chapter.lesson.ChapterLesson;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonData;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonStatement;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonsResponse;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.jerahmeel.chapter.resource.ChapterResourceStore;
import judgels.jerahmeel.role.RoleChecker;
import judgels.sandalphon.SandalphonClient;
import judgels.sandalphon.api.lesson.LessonInfo;
import judgels.sandalphon.api.lesson.LessonStatement;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/chapters/{chapterJid}/lessons")
public class ChapterLessonResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected RoleChecker roleChecker;
    @Inject protected ChapterStore chapterStore;
    @Inject protected ChapterResourceStore resourceStore;
    @Inject protected ChapterLessonStore lessonStore;
    @Inject protected SandalphonClient sandalphonClient;

    @Inject public ChapterLessonResource() {}

    @PUT
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void setLessons(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("chapterJid") String chapterJid,
            List<ChapterLessonData> data) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));
        checkAllowed(roleChecker.isAdmin(actorJid));

        Set<String> aliases = data.stream().map(ChapterLessonData::getAlias).collect(Collectors.toSet());
        Set<String> slugs = data.stream().map(ChapterLessonData::getSlug).collect(Collectors.toSet());

        checkArgument(data.size() <= 100, "Cannot set more than 100 lessons.");
        checkArgument(aliases.size() == data.size(), "Lesson aliases must be unique");
        checkArgument(slugs.size() == data.size(), "Lesson slugs must be unique");

        Map<String, String> slugToJidMap = sandalphonClient.translateAllowedLessonSlugsToJids(actorJid, slugs);

        List<ChapterLesson> setData = data.stream().filter(cp -> slugToJidMap.containsKey(cp.getSlug())).map(lesson ->
                new ChapterLesson.Builder()
                        .alias(lesson.getAlias())
                        .lessonJid(slugToJidMap.get(lesson.getSlug()))
                        .build())
                .collect(Collectors.toList());

        lessonStore.setLessons(chapterJid, setData);
    }

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ChapterLessonsResponse getLessons(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("chapterJid") String chapterJid) {

        actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));

        List<ChapterLesson> lessons = lessonStore.getLessons(chapterJid);

        var lessonJids = Lists.transform(lessons, ChapterLesson::getLessonJid);
        Map<String, LessonInfo> lessonsMap = sandalphonClient.getLessons(lessonJids);

        return new ChapterLessonsResponse.Builder()
                .data(lessons)
                .lessonsMap(lessonsMap)
                .build();
    }

    @GET
    @Path("/{lessonAlias}/statement")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ChapterLessonStatement getLessonStatement(
            @Context HttpServletRequest req,
            @Context UriInfo uriInfo,
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("chapterJid") String chapterJid,
            @PathParam("lessonAlias") String lessonAlias,
            @QueryParam("language") Optional<String> language) {

        actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));

        ChapterLesson lesson = checkFound(lessonStore.getLessonByAlias(chapterJid, lessonAlias));
        String lessonJid = lesson.getLessonJid();
        LessonInfo lessonInfo = sandalphonClient.getLesson(lessonJid);
        LessonStatement statement = sandalphonClient.getLessonStatement(req, uriInfo, lesson.getLessonJid(), language);

        List<Optional<String>> previousAndNextResourcePaths =
                resourceStore.getPreviousAndNextResourcePathsForLesson(chapterJid, lessonAlias);

        return new ChapterLessonStatement.Builder()
                .defaultLanguage(lessonInfo.getDefaultLanguage())
                .languages(lessonInfo.getTitlesByLanguage().keySet())
                .lesson(lesson)
                .statement(statement)
                .previousResourcePath(previousAndNextResourcePaths.get(0))
                .nextResourcePath(previousAndNextResourcePaths.get(1))
                .build();
    }
}
