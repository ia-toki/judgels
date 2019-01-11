package org.iatoki.judgels.jerahmeel.chapter;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.subtabLayout;
import play.i18n.Messages;

public final class ChapterControllerUtils {

    private ChapterControllerUtils() {
        // prevent instantiation
    }

    public static void appendTabLayout(LazyHtml content, Chapter chapter) {
        content.appendLayout(c -> subtabLayout.render(ImmutableList.of(
                    new InternalLink(Messages.get("chapter.update"), routes.ChapterController.editChapterGeneral(chapter.getId())),
                    new InternalLink(Messages.get("chapter.lessons"), routes.ChapterController.jumpToLessons(chapter.getId())),
                    new InternalLink(Messages.get("chapter.problems"), routes.ChapterController.jumpToProblems(chapter.getId())),
                    new InternalLink(Messages.get("chapter.dependencies"), org.iatoki.judgels.jerahmeel.chapter.dependency.routes.ChapterDependencyController.viewDependencies(chapter.getId())),
                    new InternalLink(Messages.get("chapter.submissions"), routes.ChapterController.jumpToSubmissions(chapter.getId()))
              ), c)
        );

        content.appendLayout(c -> headingLayout.render(Messages.get("chapter.chapter") + " #" + chapter.getId() + ": " + chapter.getName(), c));
    }

    public static ImmutableList.Builder<InternalLink> getBreadcrumbsBuilder() {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = ImmutableList.builder();
        breadcrumbsBuilder.add(new InternalLink(Messages.get("chapter.chapters"), routes.ChapterController.viewChapters()));

        return breadcrumbsBuilder;
    }
}
