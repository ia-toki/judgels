package org.iatoki.judgels.jerahmeel.problemset;

import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.archive.Archive;
import org.iatoki.judgels.jerahmeel.training.AbstractTrainingController;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.i18n.Messages;
import play.mvc.Result;

public abstract class AbstractProblemSetController extends AbstractTrainingController {
    protected Result renderTemplate(HtmlTemplate template, ProblemSet problemSet) {
        Archive archive = problemSet.getParentArchive();
        while (archive != null) {
            template.markBreadcrumbLocation(archive.getName(), org.iatoki.judgels.jerahmeel.archive.routes.ArchiveController.viewArchives(archive.getId()));
            archive = archive.getParentArchive();
        }


        return super.renderTemplate(template);
    }

    protected void appendTabs(HtmlTemplate template, ProblemSet problemSet) {
        template.addMainTab(Messages.get("archive.problemSet.problems"), routes.ProblemSetController.jumpToProblems(problemSet.getId()));
        template.addMainTab(Messages.get("archive.problemSet.submissions"), routes.ProblemSetController.jumpToSubmissions(problemSet.getId()));
        if (!problemSet.getDescription().isEmpty()) {
            template.setDescription(problemSet.getDescription());
        }

        template.setMainTitle(problemSet.getName());

        if (JerahmeelUtils.hasRole("admin")) {
            template.addMainButton(Messages.get("commons.button.edit"), routes.ProblemSetController.editProblemSet(problemSet.getId()));
        }
        template.setMainBackButton(Messages.get("archive.problemSet.backTo") + " " + problemSet.getParentArchive().getName(), org.iatoki.judgels.jerahmeel.archive.routes.ArchiveController.viewArchives(problemSet.getParentArchive().getId()));
    }

    protected void appendProblemSubtabs(HtmlTemplate template, ProblemSet problemSet) {
        template.markBreadcrumbLocation(Messages.get("commons.view"), org.iatoki.judgels.jerahmeel.problemset.problem.routes.ProblemSetProblemController.viewVisibleProblemSetProblems(problemSet.getId()));
        template.markBreadcrumbLocation(Messages.get("commons.manage"), org.iatoki.judgels.jerahmeel.problemset.problem.routes.ProblemSetProblemController.viewProblemSetProblems(problemSet.getId()));
    }
}
