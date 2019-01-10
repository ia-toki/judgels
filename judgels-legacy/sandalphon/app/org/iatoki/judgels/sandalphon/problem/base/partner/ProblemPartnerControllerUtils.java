package org.iatoki.judgels.sandalphon.problem.base.partner;

import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import play.i18n.Messages;

public final class ProblemPartnerControllerUtils {

    private ProblemPartnerControllerUtils() {
        // prevent instantiation
    }

    public static void appendBreadcrumbsLayout(LazyHtml content, Problem problem, InternalLink lastLink) {
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content,
                ProblemControllerUtils.getProblemBreadcrumbsBuilder(problem)
                .add(new InternalLink(Messages.get("problem.partner"), org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToPartners(problem.getId())))
                .add(lastLink)
                .build()
        );
    }
}
