package judgels.michael.lesson;

import static java.util.stream.Collectors.toSet;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.profile.ProfileStore;
import judgels.michael.actor.ActorChecker;
import judgels.michael.template.HtmlTemplate;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.lesson.LessonStore;
import judgels.sandalphon.role.RoleChecker;

@Path("/lessons")
public class LessonResource extends BaseLessonResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected RoleChecker roleChecker;
    @Inject protected LessonStore lessonStore;
    @Inject protected ProfileStore profileStore;

    @Inject public LessonResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listProblems(
            @Context HttpServletRequest req,
            @QueryParam("page") @DefaultValue("1") int pageIndex,
            @QueryParam("filter") @DefaultValue("") String filterString) {

        Actor actor = actorChecker.check(req);
        boolean isAdmin = roleChecker.isAdmin(actor);
        boolean isWriter = roleChecker.isWriter(actor);

        Page<Lesson> lessons = lessonStore.getPageOfLessons(pageIndex, "updatedAt", "desc", filterString, actor.getUserJid(), isAdmin);
        Set<String> userJids = lessons.getPage().stream().map(Lesson::getAuthorJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(Instant.now(), userJids);

        HtmlTemplate template = newLessonsTemplate(actor);
        template.setTitle("Lessons");
        if (isWriter) {
            template.addMainButton("Create", "/lessons/new");
        }
        return new ListLessonsView(template, lessons, filterString, profilesMap);
    }
}
