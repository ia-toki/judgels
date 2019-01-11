package org.iatoki.judgels.jerahmeel.controllers.api.internal;

import org.iatoki.judgels.jerahmeel.chapter.lesson.ChapterLesson;
import org.iatoki.judgels.jerahmeel.chapter.lesson.ChapterLessonNotFoundException;
import org.iatoki.judgels.jerahmeel.user.item.UserItemStatus;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.chapter.lesson.ChapterLessonService;
import org.iatoki.judgels.jerahmeel.user.item.UserItemService;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;

public final class InternalLessonAPIController extends AbstractJudgelsAPIController {

    private final ChapterLessonService chapterLessonService;
    private final UserItemService userItemService;

    @Inject
    public InternalLessonAPIController(ChapterLessonService chapterLessonService, UserItemService userItemService) {
        this.chapterLessonService = chapterLessonService;
        this.userItemService = userItemService;
    }

    @Authenticated(LoggedIn.class)
    @Transactional
    public Result updateLessonViewStatus(long chapterLessonId) throws ChapterLessonNotFoundException {
        ChapterLesson chapterLesson = chapterLessonService.findChapterLessonById(chapterLessonId);
        if (!userItemService.userItemExistsByUserJidAndItemJidAndStatus(IdentityUtils.getUserJid(), chapterLesson.getLessonJid(), UserItemStatus.COMPLETED)) {
            userItemService.upsertUserItem(IdentityUtils.getUserJid(), chapterLesson.getLessonJid(), UserItemStatus.COMPLETED, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        }

        return ok();
    }
}
