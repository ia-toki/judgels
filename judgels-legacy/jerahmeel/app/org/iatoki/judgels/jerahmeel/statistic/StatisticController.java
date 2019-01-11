package org.iatoki.judgels.jerahmeel.statistic;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.api.sandalphon.SandalphonResourceDisplayNameUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.statistic.point.PointStatistic;
import org.iatoki.judgels.jerahmeel.statistic.point.PointStatisticService;
import org.iatoki.judgels.jerahmeel.statistic.point.html.listPointStatisticsView;
import org.iatoki.judgels.jerahmeel.statistic.problem.ProblemStatisticService;
import org.iatoki.judgels.jerahmeel.statistic.problem.html.listProblemStatisticsView;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemStatistic;
import org.iatoki.judgels.jerahmeel.statistic.submission.SubmissionStatistic;
import org.iatoki.judgels.jerahmeel.statistic.submission.html.viewSubmissionStatisticsView;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.heading3Layout;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionService;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public final class StatisticController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;

    private final BundleSubmissionService bundleSubmissionService;
    private final PointStatisticService pointStatisticService;
    private final ProblemStatisticService problemStatisticService;
    private final ProgrammingSubmissionService programmingSubmissionService;

    @Inject
    public StatisticController(BundleSubmissionService bundleSubmissionService, PointStatisticService pointStatisticService, ProblemStatisticService problemStatisticService, ProgrammingSubmissionService programmingSubmissionService) {
        this.bundleSubmissionService = bundleSubmissionService;
        this.pointStatisticService = pointStatisticService;
        this.problemStatisticService = problemStatisticService;
        this.programmingSubmissionService = programmingSubmissionService;
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result index() {
        return viewPointStatistics();
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result viewPointStatistics() {
        return listPointStatistics(0, "id", "asc");
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result listPointStatistics(long pageIndex, String orderBy, String orderDir) {
        PointStatistic pointStatistic = pointStatisticService.getLatestPointStatisticWithPagination(pageIndex, PAGE_SIZE, orderBy, orderDir, "");

        LazyHtml content = new LazyHtml(listPointStatisticsView.render(pointStatistic, pageIndex, orderBy, orderDir));
        StatisticControllerUtils.appendTabLayout(content);
        content.appendLayout(c -> heading3Layout.render(Messages.get("statistic.point"), c));
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content,
                new InternalLink(Messages.get("statistic.point"), routes.StatisticController.viewPointStatistics())
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Statistics - Hall of Fame");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result viewProblemStatistics() {
        return listProblemStatistics(0, "id", "asc");
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result listProblemStatistics(long pageIndex, String orderBy, String orderDir) {
        ProblemStatistic problemStatistic = problemStatisticService.getLatestProblemStatisticWithPagination(pageIndex, PAGE_SIZE, orderBy, orderDir, "");

        List<String> problemJids = problemStatistic.getPageOfProblemStatisticEntries().getData().stream().map(d -> d.getProblemJid()).collect(Collectors.toList());
        Map<String, String> problemTitlesMap = SandalphonResourceDisplayNameUtils.buildTitlesMap(JidCacheServiceImpl.getInstance().getDisplayNames(problemJids), "en-US");

        LazyHtml content = new LazyHtml(listProblemStatisticsView.render(problemStatistic, problemTitlesMap, pageIndex, orderBy, orderDir));
        StatisticControllerUtils.appendTabLayout(content);
        content.appendLayout(c -> heading3Layout.render(Messages.get("statistic.problem") + " (" + Messages.get("statistic.problem.duration.week") + ")", c));
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content,
                new InternalLink(Messages.get("statistic.problem"), routes.StatisticController.viewProblemStatistics())
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Statistics - Favorite Problems");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional(readOnly = true)
    public Result viewSubmissionStatistics() {
        List<Long> bundleSubmissionsTime = bundleSubmissionService.getAllBundleSubmissionsSubmitTime();
        List<Long> programmingSubmissionsTime = programmingSubmissionService.getAllProgrammingSubmissionsSubmitTime();

        Collections.sort(bundleSubmissionsTime);
        Collections.reverse(bundleSubmissionsTime);
        Collections.sort(programmingSubmissionsTime);
        Collections.reverse(programmingSubmissionsTime);

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long thisHour = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        long thisDay = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        long thisWeek = calendar.getTimeInMillis();
        calendar.setTime(currentDate);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long thisMonth = calendar.getTimeInMillis();
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        long thisYear = calendar.getTimeInMillis();
        calendar.setTime(currentDate);
        calendar.add(Calendar.HOUR, -1);
        long lastHour = calendar.getTimeInMillis();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long lastDay = calendar.getTimeInMillis();
        calendar.setTime(currentDate);
        calendar.add(Calendar.WEEK_OF_MONTH, -1);
        long lastWeek = calendar.getTimeInMillis();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -30);
        long lastMonth = calendar.getTimeInMillis();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -365);
        long lastYear = calendar.getTimeInMillis();

        SubmissionStatistic bundleSubmissionStatistic = getSubmissionStatistic(bundleSubmissionsTime, thisHour, thisDay, thisWeek, thisMonth, thisYear, lastHour, lastDay, lastWeek, lastMonth, lastYear);
        SubmissionStatistic programmingSubmissionStatistic = getSubmissionStatistic(programmingSubmissionsTime, thisHour, thisDay, thisWeek, thisMonth, thisYear, lastHour, lastDay, lastWeek, lastMonth, lastYear);

        LazyHtml content = new LazyHtml(viewSubmissionStatisticsView.render(bundleSubmissionStatistic, programmingSubmissionStatistic));
        StatisticControllerUtils.appendTabLayout(content);
        content.appendLayout(c -> heading3Layout.render(Messages.get("statistic.submission"), c));
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content,
                new InternalLink(Messages.get("statistic.submission"), routes.StatisticController.viewSubmissionStatistics())
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Statistics - Submissions");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private void appendBreadcrumbsLayout(LazyHtml content, InternalLink... lastLinks) {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = StatisticControllerUtils.getBreadcrumbsBuilder();
        breadcrumbsBuilder.add(lastLinks);

        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, breadcrumbsBuilder.build());
    }

    private SubmissionStatistic getSubmissionStatistic(List<Long> submissionsTime, long thisHour, long thisDay, long thisWeek, long thisMonth, long thisYear, long lastHour, long lastDay, long lastWeek, long lastMonth, long lastYear) {
        long count = 0;
        long countThisHour = -1;
        long countThisDay = -1;
        long countThisWeek = -1;
        long countThisMonth = -1;
        long countThisYear = -1;
        long countLastHour = -1;
        long countLastDay = -1;
        long countLastWeek = -1;
        long countLastMonth = -1;
        long countLastYear = -1;
        for (Long submitTime : submissionsTime) {
            if ((countThisHour == -1) && (submitTime < thisHour)) {
                countThisHour = count;
            }
            if ((countLastHour == -1) && (submitTime < lastHour)) {
                countLastHour = count;
            }
            if ((countThisDay == -1) && (submitTime < thisDay)) {
                countThisDay = count;
            }
            if ((countLastDay == -1) && (submitTime < lastDay)) {
                countLastDay = count;
            }
            if ((countThisWeek == -1) && (submitTime < thisWeek)) {
                countThisWeek = count;
            }
            if ((countLastWeek == -1) && (submitTime < lastWeek)) {
                countLastWeek = count;
            }
            if ((countThisMonth == -1) && (submitTime < thisMonth)) {
                countThisMonth = count;
            }
            if ((countLastMonth == -1) && (submitTime < lastMonth)) {
                countLastMonth = count;
            }
            if ((countThisYear == -1) && (submitTime < thisYear)) {
                countThisYear = count;
            }
            if ((countLastYear == -1) && (submitTime < lastYear)) {
                countLastYear = count;
            }
            count++;
        }

        if (countThisHour == -1) {
            countThisHour = count;
        }
        if (countLastHour == -1) {
            countLastHour = count;
        }
        if (countThisDay == -1) {
            countThisDay = count;
        }
        if (countLastDay == -1) {
            countLastDay = count;
        }
        if (countThisWeek == -1) {
            countThisWeek = count;
        }
        if (countLastWeek == -1) {
            countLastWeek = count;
        }
        if (countThisMonth == -1) {
            countThisMonth = count;
        }
        if (countLastMonth == -1) {
            countLastMonth = count;
        }
        if (countThisYear == -1) {
            countThisYear = count;
        }
        if (countLastYear == -1) {
            countLastYear = count;
        }

        return new SubmissionStatistic(countThisHour, countThisDay, countThisWeek, countThisMonth, countThisYear, countLastHour, countLastDay, countLastWeek, countLastMonth, countLastYear, count);
    }
}
