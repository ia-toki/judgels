package org.iatoki.judgels.jerahmeel.controllers.api.internal;

import org.iatoki.judgels.AutoComplete;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapter;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterNotFoundException;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.user.item.UserItemStatus;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterService;
import org.iatoki.judgels.jerahmeel.chapter.dependency.ChapterDependencyService;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.user.item.UserItemService;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public final class InternalChapterAPIController extends AbstractJudgelsAPIController {

    private final CourseChapterService courseChapterService;
    private final ChapterDependencyService chapterDependencyService;
    private final ChapterService chapterService;
    private final UserItemService userItemService;

    @Inject
    public InternalChapterAPIController(CourseChapterService courseChapterService, ChapterDependencyService chapterDependencyService, ChapterService chapterService, UserItemService userItemService) {
        this.courseChapterService = courseChapterService;
        this.chapterDependencyService = chapterDependencyService;
        this.chapterService = chapterService;
        this.userItemService = userItemService;
    }

    @Authenticated(LoggedIn.class)
    @Transactional
    public Result autocompleteChapter(String term) {
        List<Chapter> chapters = chapterService.getChaptersByTerm(term);
        List<AutoComplete> autocompletedChapters = chapters.stream()
                .map(c -> new AutoComplete("" + c.getId(), c.getJid(), c.getName()))
                .collect(Collectors.toList());
        return okAsJson(autocompletedChapters);
    }

    @Authenticated(LoggedIn.class)
    @Transactional
    public Result updateChapterViewStatus(long courseChapterId) throws CourseChapterNotFoundException {
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);
        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());
        if (!userItemService.userItemExistsByUserJidAndItemJid(IdentityUtils.getUserJid(), chapter.getJid()) && chapterDependencyService.isDependenciesFulfilled(IdentityUtils.getUserJid(), chapter.getJid())) {
            userItemService.upsertUserItem(IdentityUtils.getUserJid(), chapter.getJid(), UserItemStatus.VIEWED, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        }

        return ok();
    }
}
