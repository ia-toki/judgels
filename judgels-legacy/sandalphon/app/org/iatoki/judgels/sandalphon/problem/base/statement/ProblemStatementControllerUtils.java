package org.iatoki.judgels.sandalphon.problem.base.statement;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.views.html.layouts.subtabLayout;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import play.i18n.Messages;

public final class ProblemStatementControllerUtils {

    private ProblemStatementControllerUtils() {
        // prevent instantiation
    }

    public static void appendSubtabsLayout(LazyHtml content, ProblemService problemService, Problem problem) {
        ImmutableList.Builder<InternalLink> internalLinks = ImmutableList.builder();

        internalLinks.add(new InternalLink(Messages.get("commons.view"), routes.ProblemStatementController.viewStatement(problem.getId())));

        if (ProblemControllerUtils.isAllowedToUpdateStatement(problemService, problem)) {
            internalLinks.add(new InternalLink(Messages.get("commons.update"), routes.ProblemStatementController.editStatement(problem.getId())));
        }

        internalLinks.add(new InternalLink(Messages.get("problem.statement.media"), routes.ProblemStatementController.listStatementMediaFiles(problem.getId())));

        if (ProblemControllerUtils.isAllowedToManageStatementLanguages(problemService, problem)) {
            internalLinks.add(new InternalLink(Messages.get("problem.statement.language"), routes.ProblemStatementController.listStatementLanguages(problem.getId())));
        }

        content.appendLayout(c -> subtabLayout.render(internalLinks.build(), c));
    }

    public static void appendBreadcrumbsLayout(LazyHtml content, Problem problem, InternalLink lastLink) {
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content,
                ProblemControllerUtils.getProblemBreadcrumbsBuilder(problem)
                        .add(new InternalLink(Messages.get("problem.statement"), org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToStatement(problem.getId())))
                        .add(lastLink)
                        .build()
        );
    }
}
