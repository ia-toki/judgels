package tlx.course.chapter;

import static com.google.common.base.Preconditions.checkArgument;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.chapter.ChapterStore;
import judgels.course.CourseStore;
import judgels.course.chapter.CourseChapterStore;
import judgels.role.TrainingAdminRoleChecker;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.stats.StatsStore;
import tlx.api.chapter.ChapterInfo;
import tlx.api.chapter.ChapterProgress;
import tlx.api.course.chapter.CourseChapter;
import tlx.api.course.chapter.CourseChaptersResponse;

@Path("/api/v2/admin/courses/{courseJid}/chapters")
public class CourseChapterAdminResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected TrainingAdminRoleChecker roleChecker;
    @Inject protected CourseStore courseStore;
    @Inject protected CourseChapterStore courseChapterStore;
    @Inject protected ChapterStore chapterStore;
    @Inject protected StatsStore statsStore;

    @Inject public CourseChapterAdminResource() {}

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
}
