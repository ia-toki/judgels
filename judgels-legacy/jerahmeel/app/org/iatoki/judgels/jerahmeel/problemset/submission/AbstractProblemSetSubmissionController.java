package org.iatoki.judgels.jerahmeel.problemset.submission;

import org.iatoki.judgels.jerahmeel.problemset.AbstractProblemSetController;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSet;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.i18n.Messages;
import play.mvc.Result;

public abstract class AbstractProblemSetSubmissionController extends AbstractProblemSetController {
    protected Result renderTemplate(HtmlTemplate template, ProblemSet problemSet) {
        template.addSecondaryTab(Messages.get("archive.problemSet.submissions.programming"), org.iatoki.judgels.jerahmeel.problemset.submission.programming.routes.ProblemSetProgrammingSubmissionController.viewOwnSubmissions(problemSet.getId()));
        template.addSecondaryTab(Messages.get("archive.problemSet.submissions.bundle"), org.iatoki.judgels.jerahmeel.problemset.submission.bundle.routes.ProblemSetBundleSubmissionController.viewOwnSubmissions(problemSet.getId()));

        template.markBreadcrumbLocation(Messages.get("archive.problemSet.submissions"), org.iatoki.judgels.jerahmeel.problemset.routes.ProblemSetController.jumpToSubmissions(problemSet.getId()));

        return super.renderTemplate(template, problemSet);
    }
}
