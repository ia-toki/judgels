package tlx.chapter.lesson;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.api.lesson.LessonInfo;
import judgels.api.lesson.LessonStatement;
import judgels.chapter.ChapterStore;
import judgels.chapter.lesson.ChapterLessonStore;
import judgels.chapter.resource.ChapterResourceStore;
import judgels.lesson.LessonService;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import tlx.api.chapter.lesson.ChapterLesson;
import tlx.api.chapter.lesson.ChapterLessonStatement;
import tlx.api.chapter.lesson.ChapterLessonsResponse;

@Path("/api/v2/chapters/{chapterJid}/lessons")
public class ChapterLessonResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected ChapterStore chapterStore;
    @Inject protected ChapterResourceStore resourceStore;
    @Inject protected ChapterLessonStore lessonStore;
    @Inject protected LessonService lessonService;

    @Inject public ChapterLessonResource() {}

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
        Map<String, LessonInfo> lessonsMap = lessonService.getLessons(lessonJids);

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
        LessonInfo lessonInfo = lessonService.getLesson(lessonJid);
        LessonStatement statement = lessonService.getLessonStatement(req, uriInfo, lesson.getLessonJid(), language);

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
