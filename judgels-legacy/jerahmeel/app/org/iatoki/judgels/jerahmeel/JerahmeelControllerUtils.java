package org.iatoki.judgels.jerahmeel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.iatoki.judgels.api.jophiel.JophielClientAPI;
import org.iatoki.judgels.api.jophiel.JophielPublicAPI;
import org.iatoki.judgels.api.sandalphon.SandalphonResourceDisplayNameUtils;
import org.iatoki.judgels.jerahmeel.activity.ActivityLogServiceImpl;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.statistic.point.PointStatistic;
import org.iatoki.judgels.jerahmeel.statistic.point.PointStatisticService;
import org.iatoki.judgels.jerahmeel.statistic.point.html.pointStatisticView;
import org.iatoki.judgels.jerahmeel.statistic.problem.ProblemStatisticService;
import org.iatoki.judgels.jerahmeel.statistic.problem.html.problemStatisticView;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemScoreStatistic;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemScoreStatisticService;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemStatistic;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.html.problemScoreStatisticLayout;
import org.iatoki.judgels.jerahmeel.statistic.submission.SubmissionEntry;
import org.iatoki.judgels.jerahmeel.statistic.submission.html.recentSubmissionView;
import org.iatoki.judgels.jophiel.JophielClientControllerUtils;
import org.iatoki.judgels.jophiel.activity.ActivityKey;
import org.iatoki.judgels.jophiel.activity.UserActivityMessage;
import org.iatoki.judgels.jophiel.activity.UserActivityMessageServiceImpl;
import org.iatoki.judgels.jophiel.logincheck.html.isLoggedInLayout;
import org.iatoki.judgels.jophiel.logincheck.html.isLoggedOutLayout;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsControllerUtils;
import org.iatoki.judgels.play.controllers.ControllerUtils;
import org.iatoki.judgels.play.views.html.layouts.contentLayout;
import org.iatoki.judgels.play.views.html.layouts.guestLoginView;
import org.iatoki.judgels.play.views.html.layouts.menusLayout;
import org.iatoki.judgels.play.views.html.layouts.profileView;
import org.iatoki.judgels.play.views.html.layouts.sidebarLayout;
import org.iatoki.judgels.play.views.html.layouts.threeWidgetLayout;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmission;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionService;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmission;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;
import play.i18n.Messages;
import play.mvc.Http;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class JerahmeelControllerUtils extends AbstractJudgelsControllerUtils {

    private static JerahmeelControllerUtils instance;

    private final JophielClientAPI jophielClientAPI;
    private final JophielPublicAPI jophielPublicAPI;
    private final BundleSubmissionService bundleSubmissionService;
    private final PointStatisticService pointStatisticService;
    private final ProblemScoreStatisticService problemScoreStatisticService;
    private final ProblemStatisticService problemStatisticService;
    private final ProgrammingSubmissionService programmingSubmissionService;

    public JerahmeelControllerUtils(JophielClientAPI jophielClientAPI, JophielPublicAPI jophielPublicAPI, BundleSubmissionService bundleSubmissionService, PointStatisticService pointStatisticService, ProblemScoreStatisticService problemScoreStatisticService, ProblemStatisticService problemStatisticService, ProgrammingSubmissionService programmingSubmissionService) {
        this.jophielClientAPI = jophielClientAPI;
        this.jophielPublicAPI = jophielPublicAPI;
        this.bundleSubmissionService = bundleSubmissionService;
        this.pointStatisticService = pointStatisticService;
        this.problemScoreStatisticService = problemScoreStatisticService;
        this.problemStatisticService = problemStatisticService;
        this.programmingSubmissionService = programmingSubmissionService;
    }

    public static synchronized void buildInstance(JophielClientAPI jophielClientAPI, JophielPublicAPI jophielPublicAPI, BundleSubmissionService bundleSubmissionService, PointStatisticService pointStatisticService, ProblemScoreStatisticService problemScoreStatisticService, ProblemStatisticService problemStatisticService, ProgrammingSubmissionService programmingSubmissionService) {
        if (instance != null) {
            throw new UnsupportedOperationException("JerahmeelControllerUtils instance has already been built");
        }
        instance = new JerahmeelControllerUtils(jophielClientAPI, jophielPublicAPI, bundleSubmissionService, pointStatisticService, problemScoreStatisticService, problemStatisticService, programmingSubmissionService);
    }

    public static JerahmeelControllerUtils getInstance() {
        if (instance == null) {
            throw new UnsupportedOperationException("JerahmeelControllerUtils instance has not been built");
        }
        return instance;
    }

    @SuppressWarnings("checkstyle:simplifybooleanexpression")
    @Override
    public void appendSidebarLayout(LazyHtml content) {
        content.appendLayout(c -> contentLayout.render(c));

        if (Http.Context.current().session().containsKey("problemJid") && false) {
            String problemJid = Http.Context.current().session().get("problemJid");
            Http.Context.current().session().remove("problemJid");

            addProblemWidget(content, problemJid);
        }

        ImmutableList.Builder<InternalLink> internalLinkBuilder = ImmutableList.builder();
        internalLinkBuilder.add(new InternalLink(Messages.get("training.training"), org.iatoki.judgels.jerahmeel.training.routes.TrainingController.index()));
        internalLinkBuilder.add(new InternalLink(Messages.get("submission.submissions"), org.iatoki.judgels.jerahmeel.submission.routes.SubmissionController.jumpToSubmissions()));
        // internalLinkBuilder.add(new InternalLink(Messages.get("statistic.statistics"), org.iatoki.judgels.jerahmeel.statistic.routes.StatisticController.index()));
        if (isAdmin()) {
            internalLinkBuilder.add(new InternalLink(Messages.get("curriculum.curriculums"), org.iatoki.judgels.jerahmeel.curriculum.routes.CurriculumController.viewCurriculums()));
            internalLinkBuilder.add(new InternalLink(Messages.get("course.courses"), org.iatoki.judgels.jerahmeel.course.routes.CourseController.viewCourses()));
            internalLinkBuilder.add(new InternalLink(Messages.get("chapter.chapters"), org.iatoki.judgels.jerahmeel.chapter.routes.ChapterController.viewChapters()));
            internalLinkBuilder.add(new InternalLink(Messages.get("user.users"), org.iatoki.judgels.jerahmeel.user.routes.UserController.index()));
        }
        LazyHtml sidebarContent;
        if (JerahmeelUtils.isGuest()) {
            sidebarContent = new LazyHtml(guestLoginView.render(routes.ApplicationController.auth(ControllerUtils.getCurrentUrl(Http.Context.current().request())).absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure()), JophielClientControllerUtils.getInstance().getRegisterUrl().toString()));
        } else {
            sidebarContent = new LazyHtml(profileView.render(
                    IdentityUtils.getUsername(),
                    IdentityUtils.getUserRealName(),
                    org.iatoki.judgels.jophiel.routes.JophielClientController.profile().absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure()),
                    org.iatoki.judgels.jophiel.routes.JophielClientController.logout(ControllerUtils.getCurrentUrl(Http.Context.current().request())).absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure())
                ));
        }
        sidebarContent.appendLayout(c -> menusLayout.render(internalLinkBuilder.build(), c));

        content.appendLayout(c -> sidebarLayout.render(sidebarContent.render(), c));

        if (isInTrainingMainPage()) {
            addWidgets(content);
        }

        if (JerahmeelUtils.isGuest()) {
            content.appendLayout(c -> isLoggedInLayout.render(jophielClientAPI.getUserIsLoggedInAPIEndpoint(), routes.ApplicationController.auth(ControllerUtils.getCurrentUrl(Http.Context.current().request())).absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure()), "lib/jophielcommons/javascripts/isLoggedIn.js", c));
        } else {
            content.appendLayout(c -> isLoggedOutLayout.render(jophielClientAPI.getUserIsLoggedInAPIEndpoint(), routes.ApplicationController.logout(ControllerUtils.getCurrentUrl(Http.Context.current().request())).absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure()), "lib/jophielcommons/javascripts/isLoggedOut.js", JerahmeelUtils.getRealUserJid(), c));
        }
    }

    public boolean isAdmin() {
        return JerahmeelUtils.hasRole("admin");
    }

    public void addActivityLog(ActivityKey activityKey) {
        if (!JerahmeelUtils.isGuest()) {
            long time = System.currentTimeMillis();
            ActivityLogServiceImpl.getInstance().addActivityLog(activityKey, JerahmeelUtils.getRealUsername(), time, JerahmeelUtils.getRealUserJid(), IdentityUtils.getIpAddress());
            String log = JerahmeelUtils.getRealUsername() + " " + activityKey.toString();
            try {
                if (JudgelsPlayUtils.hasViewPoint()) {
                    log += " view as " + IdentityUtils.getUsername();
                }
                UserActivityMessageServiceImpl.getInstance().addUserActivityMessage(new UserActivityMessage(System.currentTimeMillis(), JerahmeelUtils.getRealUserJid(), log, IdentityUtils.getIpAddress()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isInTrainingMainPage() {
        return ControllerUtils.getCurrentUrl(Http.Context.current().request()).equals(org.iatoki.judgels.jerahmeel.training.routes.TrainingController.index().absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure()));
    }

    private void addProblemWidget(LazyHtml content, String problemJid) {
        if (problemScoreStatisticService.problemScoreStatisticExists(problemJid)) {
            ProblemScoreStatistic problemScoreStatistic = problemScoreStatisticService.getLatestProblemScoreStatisticWithPagination(problemJid, 0, 5, "id", "asc", "");
            content.appendLayout(c -> problemScoreStatisticLayout.render(problemScoreStatistic, c));
        }
    }

    private void addWidgets(LazyHtml content) {
        PointStatistic pointStatistic;
        if (pointStatisticService.pointStatisticExists()) {
            pointStatistic = pointStatisticService.getLatestPointStatisticWithPagination(0, 5, "id", "asc", "");
        } else {
            pointStatistic = null;
        }

        ProblemStatistic problemStatistic;
        if (problemStatisticService.problemStatisticExists()) {
            problemStatistic = problemStatisticService.getLatestProblemStatisticWithPagination(0, 5, "id", "asc", "");
        } else {
            problemStatistic = null;
        }
        List<String> problemStatisticProblemJids = problemStatistic.getPageOfProblemStatisticEntries().getData().stream().map(e -> e.getProblemJid()).collect(Collectors.toList());
        Map<String, String> problemStatisticsTitleMap = SandalphonResourceDisplayNameUtils.buildTitlesMap(JidCacheServiceImpl.getInstance().getDisplayNames(problemStatisticProblemJids), "en-US");

        List<SubmissionEntry> submissionEntries = Lists.newArrayList();
        Page<BundleSubmission> pageOfBundleSubmissions = bundleSubmissionService.getPageOfBundleSubmissions(0, 5, "timeCreate", "desc", null, null, null);
        Page<ProgrammingSubmission> pageOfProgrammingSubmissions = programmingSubmissionService.getPageOfProgrammingSubmissions(0, 5, "timeCreate", "desc", null, null, null);
        for (BundleSubmission bundleSubmission : pageOfBundleSubmissions.getData()) {
            submissionEntries.add(new SubmissionEntry(bundleSubmission.getAuthorJid(), bundleSubmission.getProblemJid(), bundleSubmission.getLatestScore(), bundleSubmission.getTime().getTime()));
        }
        for (ProgrammingSubmission programmingSubmission : pageOfProgrammingSubmissions.getData()) {
            submissionEntries.add(new SubmissionEntry(programmingSubmission.getAuthorJid(), programmingSubmission.getProblemJid(), programmingSubmission.getLatestScore(), programmingSubmission.getTime().getTime()));
        }

        List<String> problemJids = submissionEntries.stream().map(e -> e.getProblemJid()).collect(Collectors.toList());
        Map<String, String> problemTitlesMap = SandalphonResourceDisplayNameUtils.buildTitlesMap(JidCacheServiceImpl.getInstance().getDisplayNames(problemJids), "en-US");

        Collections.sort(submissionEntries);

        // content.appendLayout(c -> threeWidgetLayout.render(pointStatisticView.render(pointStatistic), problemStatisticView.render(problemStatistic, problemStatisticsTitleMap), recentSubmissionView.render(submissionEntries, problemTitlesMap), c));
    }
}
