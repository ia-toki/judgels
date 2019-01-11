package org.iatoki.judgels.jerahmeel.statistic;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.views.html.layouts.tabLayout;
import play.i18n.Messages;

final class StatisticControllerUtils {

    private StatisticControllerUtils() {
        // prevent instantiation
    }

    static void appendTabLayout(LazyHtml content) {
        ImmutableList.Builder<InternalLink> tabBuilder = ImmutableList.builder();
        tabBuilder.add(new InternalLink(Messages.get("statistic.point"), routes.StatisticController.viewPointStatistics()));
        tabBuilder.add(new InternalLink(Messages.get("statistic.problem"), routes.StatisticController.viewProblemStatistics()));
        if (JerahmeelUtils.hasRole("admin")) {
            tabBuilder.add(new InternalLink(Messages.get("statistic.submission"), routes.StatisticController.viewSubmissionStatistics()));
        }
        content.appendLayout(c -> tabLayout.render(tabBuilder.build(), c));
    }

    static ImmutableList.Builder<InternalLink> getBreadcrumbsBuilder() {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = ImmutableList.builder();
        breadcrumbsBuilder.add(new InternalLink(Messages.get("statistic.statistics"), routes.StatisticController.index()));

        return breadcrumbsBuilder;
    }
}
