package org.iatoki.judgels.jerahmeel.submission;

import com.google.common.collect.ImmutableMap;
import org.iatoki.judgels.jerahmeel.AbstractJerahmeelController;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetService;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.i18n.Messages;
import play.mvc.Result;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractSubmissionController extends AbstractJerahmeelController  {
    protected Result renderTemplate(HtmlTemplate template) {
        template.markBreadcrumbLocation(Messages.get("submission.submissions"), routes.SubmissionController.jumpToSubmissions());

        return super.renderTemplate(template);
    }

    protected void appendTabs(HtmlTemplate template) {
        template.addMainTab(Messages.get("submission.own"), routes.SubmissionController.jumpToOwnSubmissions());
        template.addMainTab(Messages.get("submission.all"), routes.SubmissionController.jumpToAllSubmissions());
    }

    protected void appendOwnSubtabs(HtmlTemplate template) {
        template.addSecondaryTab(Messages.get("submission.programming"), org.iatoki.judgels.jerahmeel.submission.programming.routes.ProgrammingSubmissionController.viewOwnSubmissions());
        template.addSecondaryTab(Messages.get("submission.bundle"), org.iatoki.judgels.jerahmeel.submission.bundle.routes.BundleSubmissionController.viewOwnSubmissions());
    }

    protected void appendAllSubtabs(HtmlTemplate template) {
        template.addSecondaryTab(Messages.get("submission.programming"), org.iatoki.judgels.jerahmeel.submission.programming.routes.ProgrammingSubmissionController.viewSubmissions());
        template.addSecondaryTab(Messages.get("submission.bundle"), org.iatoki.judgels.jerahmeel.submission.bundle.routes.BundleSubmissionController.viewSubmissions());
    }

    protected Map<String, String> getJidToNameMap(ChapterService chapterService, ProblemSetService problemSetService, List<String> jids) {
        Map<String, String> chapterJidToNameMap = chapterService.getChapterJidToNameMapByChapterJids(jids.stream().filter(s -> s.startsWith("JIDSESS")).collect(Collectors.toList()));
        Map<String, String> problemSetJidToNameMap = problemSetService.getProblemSetJidToNameMapByProblemSetJids(jids.stream().filter(s -> s.startsWith("JIDPRSE")).collect(Collectors.toList()));

        ImmutableMap.Builder<String, String> jidToNameMapBuilder = ImmutableMap.builder();
        jidToNameMapBuilder.putAll(chapterJidToNameMap);
        jidToNameMapBuilder.putAll(problemSetJidToNameMap);

        return jidToNameMapBuilder.build();
    }
}
