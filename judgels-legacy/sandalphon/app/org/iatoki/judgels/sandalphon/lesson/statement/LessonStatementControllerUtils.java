package org.iatoki.judgels.sandalphon.lesson.statement;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.views.html.layouts.subtabLayout;
import org.iatoki.judgels.sandalphon.lesson.Lesson;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.lesson.LessonControllerUtils;
import org.iatoki.judgels.sandalphon.lesson.LessonService;
import play.i18n.Messages;

public final class LessonStatementControllerUtils {

    private LessonStatementControllerUtils() {
        // prevent instantiation
    }

    public static void appendSubtabsLayout(LazyHtml content, LessonService lessonService, Lesson lesson) {
        ImmutableList.Builder<InternalLink> internalLinks = ImmutableList.builder();

        internalLinks.add(new InternalLink(Messages.get("commons.view"), routes.LessonStatementController.viewStatement(lesson.getId())));

        if (LessonControllerUtils.isAllowedToUpdateStatement(lessonService, lesson)) {
            internalLinks.add(new InternalLink(Messages.get("commons.update"), routes.LessonStatementController.editStatement(lesson.getId())));
        }

        internalLinks.add(new InternalLink(Messages.get("lesson.statement.media"), routes.LessonStatementController.listStatementMediaFiles(lesson.getId())));

        if (LessonControllerUtils.isAllowedToManageStatementLanguages(lessonService, lesson)) {
            internalLinks.add(new InternalLink(Messages.get("lesson.statement.language"), routes.LessonStatementController.listStatementLanguages(lesson.getId())));
        }

        content.appendLayout(c -> subtabLayout.render(internalLinks.build(), c));
    }

    public static void appendBreadcrumbsLayout(LazyHtml content, Lesson lesson, InternalLink lastLink) {
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content,
                LessonControllerUtils.getLessonBreadcrumbsBuilder(lesson)
                        .add(new InternalLink(Messages.get("lesson.statement"), org.iatoki.judgels.sandalphon.lesson.routes.LessonController.jumpToStatement(lesson.getId())))
                        .add(lastLink)
                        .build()
        );
    }
}
