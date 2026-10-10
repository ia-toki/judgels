package judgels.catalog.lesson;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.core.JudgelsRequestChecks.checkAllowed;
import static judgels.core.JudgelsRequestChecks.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.Optional;
import java.util.Set;
import judgels.api.catalog.lesson.Lesson;
import judgels.api.catalog.lesson.LessonConfig;
import judgels.api.catalog.lesson.LessonCreateData;
import judgels.api.catalog.lesson.LessonErrors;
import judgels.api.catalog.lesson.LessonResponse;
import judgels.api.catalog.lesson.LessonUpdateData;
import judgels.api.catalog.lesson.LessonsResponse;
import judgels.catalog.WorldLanguageRegistry;
import judgels.core.api.AuthHeader;
import judgels.persistence.api.Page;
import judgels.profile.ProfileStore;
import judgels.session.ActorChecker;

@Path("/api/v4/lessons")
public class LessonResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject protected LessonRoleChecker roleChecker;
    @Inject protected LessonStore lessonStore;
    @Inject protected LessonCreator lessonCreator;
    @Inject protected ProfileStore profileStore;

    @Inject public LessonResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public LessonsResponse getLessons(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("term") @DefaultValue("") String termFilter,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);

        Optional<String> userJid = roleChecker.isAdmin(actorJid) ? Optional.empty() : Optional.of(actorJid);
        Page<Lesson> lessons = lessonStore.getLessons(userJid, termFilter, pageNumber, PAGE_SIZE);

        var authorJids = Lists.transform(lessons.getPage(), Lesson::getAuthorJid);

        return new LessonsResponse.Builder()
                .data(lessons)
                .profilesMap(profileStore.getProfiles(authorJids))
                .build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Lesson createLesson(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            LessonCreateData data) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(data.getInitialLanguage())) {
            throw new BadRequestException();
        }
        if (lessonStore.lessonExistsBySlug(data.getSlug())) {
            throw LessonErrors.slugAlreadyExists(data.getSlug());
        }

        return lessonCreator.createLesson(
                actorJid,
                data.getSlug(),
                data.getAdditionalNote(),
                data.getInitialLanguage());
    }

    @GET
    @Path("/{lessonJid}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public LessonResponse getLesson(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid) {

        String actorJid = actorChecker.check(authHeader);
        Lesson lesson = checkFound(lessonStore.getLessonByJid(lessonJid));
        checkAllowed(roleChecker.canView(actorJid, lesson));

        return new LessonResponse.Builder()
                .data(lesson)
                .hasLocalChanges(lessonStore.userCloneExists(actorJid, lessonJid))
                .config(new LessonConfig.Builder()
                        .canEdit(roleChecker.canEdit(actorJid, lesson))
                        .build())
                .profilesMap(profileStore.getProfiles(Set.of(lesson.getAuthorJid())))
                .build();
    }

    @GET
    @Path("/slug/{lessonSlug}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Lesson getLessonBySlug(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonSlug") String lessonSlug) {

        String actorJid = actorChecker.check(authHeader);
        Lesson lesson = checkFound(lessonStore.getLessonBySlug(lessonSlug));
        checkAllowed(roleChecker.canView(actorJid, lesson));

        return lesson;
    }

    @POST
    @Path("/{lessonJid}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Lesson updateLesson(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid,
            LessonUpdateData data) {

        String actorJid = actorChecker.check(authHeader);
        Lesson lesson = checkFound(lessonStore.getLessonByJid(lessonJid));
        checkAllowed(roleChecker.canEdit(actorJid, lesson));

        if (!lesson.getSlug().equals(data.getSlug()) && lessonStore.lessonExistsBySlug(data.getSlug())) {
            throw LessonErrors.slugAlreadyExists(data.getSlug());
        }

        lessonStore.updateLesson(lessonJid, data.getSlug(), data.getAdditionalNote());

        return checkFound(lessonStore.getLessonByJid(lessonJid));
    }
}
