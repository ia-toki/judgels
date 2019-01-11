package org.iatoki.judgels.jerahmeel.submission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetService;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.views.html.layouts.subtabLayout;
import org.iatoki.judgels.play.views.html.layouts.tabLayout;
import play.i18n.Messages;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class SubmissionControllerUtils {

    private SubmissionControllerUtils() {
        // prevent instantiation
    }

    public static Map<String, String> getJidToNameMap(ChapterService chapterService, ProblemSetService problemSetService, List<String> jids) {
        Map<String, String> chapterJidToNameMap = chapterService.getChapterJidToNameMapByChapterJids(jids.stream().filter(s -> s.startsWith("JIDSESS")).collect(Collectors.toList()));
        Map<String, String> problemSetJidToNameMap = problemSetService.getProblemSetJidToNameMapByProblemSetJids(jids.stream().filter(s -> s.startsWith("JIDPRSE")).collect(Collectors.toList()));

        ImmutableMap.Builder<String, String> jidToNameMapBuilder = ImmutableMap.builder();
        jidToNameMapBuilder.putAll(chapterJidToNameMap);
        jidToNameMapBuilder.putAll(problemSetJidToNameMap);

        return jidToNameMapBuilder.build();
    }

    public static void appendTabLayout(LazyHtml content) {
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
                        new InternalLink(Messages.get("submission.own"), routes.SubmissionController.jumpToOwnSubmissions()),
                        new InternalLink(Messages.get("submission.all"), routes.SubmissionController.jumpToAllSubmissions())
                ), c)
        );
    }

    public static void appendOwnSubtabLayout(LazyHtml content) {
        content.appendLayout(c -> subtabLayout.render(ImmutableList.of(
                        new InternalLink(Messages.get("submission.programming"), org.iatoki.judgels.jerahmeel.submission.programming.routes.ProgrammingSubmissionController.viewOwnSubmissions()),
                        new InternalLink(Messages.get("submission.bundle"), org.iatoki.judgels.jerahmeel.submission.bundle.routes.BundleSubmissionController.viewOwnSubmissions())
                ), c)
        );
    }

    public static void appendAllSubtabLayout(LazyHtml content) {
        content.appendLayout(c -> subtabLayout.render(ImmutableList.of(
                        new InternalLink(Messages.get("submission.programming"), org.iatoki.judgels.jerahmeel.submission.programming.routes.ProgrammingSubmissionController.viewSubmissions()),
                        new InternalLink(Messages.get("submission.bundle"), org.iatoki.judgels.jerahmeel.submission.bundle.routes.BundleSubmissionController.viewSubmissions())
                ), c)
        );
    }

    public static ImmutableList.Builder<InternalLink> getBreadcrumbsBuilder() {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = ImmutableList.builder();
        breadcrumbsBuilder.add(new InternalLink(Messages.get("submission.submissions"), routes.SubmissionController.jumpToSubmissions()));

        return breadcrumbsBuilder;
    }
}
