package org.iatoki.judgels.jerahmeel.controllers.api.internal;

import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblem;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemNotFoundException;
import org.iatoki.judgels.jerahmeel.user.item.UserItemStatus;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemService;
import org.iatoki.judgels.jerahmeel.user.item.UserItemService;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;

public final class InternalProblemAPIController extends AbstractJudgelsAPIController {

    private final ChapterProblemService chapterProblemService;
    private final UserItemService userItemService;

    @Inject
    public InternalProblemAPIController(ChapterProblemService chapterProblemService, UserItemService userItemService) {
        this.chapterProblemService = chapterProblemService;
        this.userItemService = userItemService;
    }

    @Authenticated(LoggedIn.class)
    @Transactional
    public Result updateProblemViewStatus(long chapterProblemId) throws ChapterProblemNotFoundException {
        ChapterProblem chapterProblem = chapterProblemService.findChapterProblemById(chapterProblemId);
        if (!userItemService.userItemExistsByUserJidAndItemJidAndStatus(IdentityUtils.getUserJid(), chapterProblem.getProblemJid(), UserItemStatus.COMPLETED)) {
            userItemService.upsertUserItem(IdentityUtils.getUserJid(), chapterProblem.getProblemJid(), UserItemStatus.VIEWED, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        }

        return ok();
    }
}
