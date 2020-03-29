package judgels.jerahmeel.chapter;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterCreateData;
import judgels.jerahmeel.api.chapter.ChapterService;
import judgels.jerahmeel.api.chapter.ChapterUpdateData;
import judgels.jerahmeel.api.chapter.ChaptersResponse;
import judgels.jerahmeel.role.RoleChecker;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ChapterResource implements ChapterService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ChapterStore chapterStore;

    @Inject
    public ChapterResource(ActorChecker actorChecker, RoleChecker roleChecker, ChapterStore chapterStore) {
        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.chapterStore = chapterStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ChaptersResponse getChapters(AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        List<Chapter> chapters = chapterStore.getChapters();
        return new ChaptersResponse.Builder()
                .data(chapters)
                .build();
    }

    @Override
    @UnitOfWork
    public Chapter createChapter(AuthHeader authHeader, ChapterCreateData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        return chapterStore.createChapter(data);
    }

    @Override
    @UnitOfWork
    public Chapter updateChapter(AuthHeader authHeader, String chapterJid, ChapterUpdateData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        return checkFound(chapterStore.updateChapter(chapterJid, data));
    }
}
