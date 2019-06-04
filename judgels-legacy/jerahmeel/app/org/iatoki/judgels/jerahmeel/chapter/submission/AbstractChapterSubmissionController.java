package org.iatoki.judgels.jerahmeel.chapter.submission;

import org.iatoki.judgels.jerahmeel.chapter.AbstractChapterController;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.i18n.Messages;
import play.mvc.Result;

public abstract class AbstractChapterSubmissionController extends AbstractChapterController {
    protected Result renderTemplate(HtmlTemplate template, Chapter chapter) {
        appendTabs(template, chapter);

        template.addSecondaryTab(Messages.get("chapter.submissions.programming"), org.iatoki.judgels.jerahmeel.chapter.routes.ChapterController.jumpToProgrammingSubmissions(chapter.getId()));
        template.addSecondaryTab(Messages.get("chapter.submissions.bundle"), org.iatoki.judgels.jerahmeel.chapter.routes.ChapterController.jumpToBundleSubmissions(chapter.getId()));

        template.markBreadcrumbLocation(Messages.get("chapter.submissions"), org.iatoki.judgels.jerahmeel.chapter.routes.ChapterController.jumpToSubmissions(chapter.getId()));

        return super.renderTemplate(template);
    }
}
