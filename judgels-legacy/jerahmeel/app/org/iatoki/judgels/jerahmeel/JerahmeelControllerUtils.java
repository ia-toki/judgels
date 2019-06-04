package org.iatoki.judgels.jerahmeel;

import com.google.common.collect.Lists;
import org.iatoki.judgels.api.sandalphon.SandalphonResourceDisplayNameUtils;
import org.iatoki.judgels.jerahmeel.activity.ActivityLogServiceImpl;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.statistic.point.PointStatistic;
import org.iatoki.judgels.jerahmeel.statistic.point.PointStatisticService;
import org.iatoki.judgels.jerahmeel.statistic.problem.ProblemStatisticService;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemScoreStatistic;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemScoreStatisticService;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemStatistic;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.html.problemScoreStatisticLayout;
import org.iatoki.judgels.jerahmeel.statistic.submission.SubmissionEntry;
import org.iatoki.judgels.jophiel.activity.ActivityKey;
import org.iatoki.judgels.jophiel.activity.UserActivityMessage;
import org.iatoki.judgels.jophiel.activity.UserActivityMessageServiceImpl;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.ControllerUtils;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmission;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionService;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmission;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;
import play.mvc.Http;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class JerahmeelControllerUtils {

    private static JerahmeelControllerUtils instance;

    private final BundleSubmissionService bundleSubmissionService;
    private final PointStatisticService pointStatisticService;
    private final ProblemScoreStatisticService problemScoreStatisticService;
    private final ProblemStatisticService problemStatisticService;
    private final ProgrammingSubmissionService programmingSubmissionService;

    public JerahmeelControllerUtils(BundleSubmissionService bundleSubmissionService, PointStatisticService pointStatisticService, ProblemScoreStatisticService problemScoreStatisticService, ProblemStatisticService problemStatisticService, ProgrammingSubmissionService programmingSubmissionService) {
        this.bundleSubmissionService = bundleSubmissionService;
        this.pointStatisticService = pointStatisticService;
        this.problemScoreStatisticService = problemScoreStatisticService;
        this.problemStatisticService = problemStatisticService;
        this.programmingSubmissionService = programmingSubmissionService;
    }

    public static synchronized void buildInstance(BundleSubmissionService bundleSubmissionService, PointStatisticService pointStatisticService, ProblemScoreStatisticService problemScoreStatisticService, ProblemStatisticService problemStatisticService, ProgrammingSubmissionService programmingSubmissionService) {
        if (instance != null) {
            throw new UnsupportedOperationException("JerahmeelControllerUtils instance has already been built");
        }
        instance = new JerahmeelControllerUtils(bundleSubmissionService, pointStatisticService, problemScoreStatisticService, problemStatisticService, programmingSubmissionService);
    }

    public static JerahmeelControllerUtils getInstance() {
        if (instance == null) {
            throw new UnsupportedOperationException("JerahmeelControllerUtils instance has not been built");
        }
        return instance;
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
        Page<BundleSubmission> pageOfBundleSubmissions = bundleSubmissionService.getPageOfBundleSubmissions(0, 5, "createdAt", "desc", null, null, null);
        Page<ProgrammingSubmission> pageOfProgrammingSubmissions = programmingSubmissionService.getPageOfProgrammingSubmissions(0, 5, "createdAt", "desc", null, null, null);
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
