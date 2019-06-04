package org.iatoki.judgels.jerahmeel.chapter;

import org.iatoki.judgels.jerahmeel.AbstractJerahmeelController;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.i18n.Messages;
import play.mvc.Result;

public abstract class AbstractChapterController extends AbstractJerahmeelController {
    @Override
    protected Result renderTemplate(HtmlTemplate template) {
        template.markBreadcrumbLocation(Messages.get("chapter.chapters"), routes.ChapterController.viewChapters());

        return super.renderTemplate(template);
    }

    protected void appendTabs(HtmlTemplate template, Chapter chapter) {
        template.addMainTab(Messages.get("chapter.update"), routes.ChapterController.editChapterGeneral(chapter.getId()));
        template.addMainTab(Messages.get("chapter.lessons"), routes.ChapterController.jumpToLessons(chapter.getId()));
        template.addMainTab(Messages.get("chapter.problems"), routes.ChapterController.jumpToProblems(chapter.getId()));
        template.addMainTab(Messages.get("chapter.dependencies"), org.iatoki.judgels.jerahmeel.chapter.dependency.routes.ChapterDependencyController.viewDependencies(chapter.getId()));
        template.addMainTab(Messages.get("chapter.submissions"), routes.ChapterController.jumpToSubmissions(chapter.getId()));

        template.setMainTitle(Messages.get("chapter.chapter") + " #" + chapter.getId() + ": " + chapter.getName());
    }
}
