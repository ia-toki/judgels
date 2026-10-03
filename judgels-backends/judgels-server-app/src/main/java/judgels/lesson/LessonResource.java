package judgels.lesson;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.Optional;
import judgels.api.lesson.Lesson;
import judgels.api.lesson.LessonsResponse;
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
}
