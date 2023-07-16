package judgels.jerahmeel.chapter.lesson;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import judgels.jerahmeel.api.chapter.lesson.ChapterLesson;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonData;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonStatement;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonsResponse;
import judgels.jerahmeel.chapter.ChapterStore;
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

        return new ChapterLessonStatement.Builder()
                .defaultLanguage(lessonInfo.getDefaultLanguage())
                .languages(lessonInfo.getTitlesByLanguage().keySet())
                .lesson(lesson)
                .statement(statement)
                .build();
    }
}
