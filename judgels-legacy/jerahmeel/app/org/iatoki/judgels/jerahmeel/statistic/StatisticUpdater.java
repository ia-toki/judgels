package org.iatoki.judgels.jerahmeel.statistic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.iatoki.judgels.jerahmeel.statistic.point.PointStatisticEntry;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemScoreStatisticEntry;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemStatisticEntry;
import org.iatoki.judgels.jerahmeel.statistic.point.PointStatisticService;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemScoreStatisticService;
import org.iatoki.judgels.jerahmeel.statistic.problem.ProblemStatisticService;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmission;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmission;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionService;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;
import play.db.jpa.JPAApi;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class StatisticUpdater implements Runnable {

    private final JPAApi jpaApi;
    private final BundleSubmissionService bundleSubmissionService;
    private final PointStatisticService pointStatisticService;
    private final ProblemScoreStatisticService problemScoreStatisticService;
    private final ProblemStatisticService problemStatisticService;
    private final ProgrammingSubmissionService programmingSubmissionService;

    public StatisticUpdater(JPAApi jpaApi, BundleSubmissionService bundleSubmissionService, PointStatisticService pointStatisticService, ProblemScoreStatisticService problemScoreStatisticService, ProblemStatisticService problemStatisticService, ProgrammingSubmissionService programmingSubmissionService) {
        this.jpaApi = jpaApi;
        this.bundleSubmissionService = bundleSubmissionService;
        this.pointStatisticService = pointStatisticService;
        this.problemScoreStatisticService = problemScoreStatisticService;
        this.problemStatisticService = problemStatisticService;
        this.programmingSubmissionService = programmingSubmissionService;
    }

    @Override
    public void run() {
        jpaApi.withTransaction(() -> {
                long timeNow = System.currentTimeMillis();
                Map<String, Map<String, Double>> userJidToMapProblemJidToPoints = Maps.newHashMap();
                Map<String, Long> problemJidToTotalSubmissions = Maps.newHashMap();
                Map<String, Map<String, PointAndTime>> problemJidToMapUserJidToPoints = Maps.newHashMap();

                List<BundleSubmission> bundleSubmissions = bundleSubmissionService.getAllBundleSubmissions();
                for (BundleSubmission bundleSubmission : bundleSubmissions) {
                    String userJid = bundleSubmission.getAuthorJid();
                    Map<String, Double> problemJidToPoints;
                    if (userJidToMapProblemJidToPoints.containsKey(userJid)) {
                        problemJidToPoints = userJidToMapProblemJidToPoints.get(userJid);
                    } else {
                        problemJidToPoints = Maps.newHashMap();
                    }

                    String problemJid = bundleSubmission.getProblemJid();
                    double point;
                    if (problemJidToPoints.containsKey(problemJid)) {
                        point = problemJidToPoints.get(problemJid);
                    } else {
                        point = -1;
                    }
                    if (bundleSubmission.getLatestScore() > point) {
                        problemJidToPoints.put(problemJid, bundleSubmission.getLatestScore());
                    }

                    userJidToMapProblemJidToPoints.put(userJid, problemJidToPoints);

                    if ((timeNow - bundleSubmission.getTime().getTime()) <= TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS)) {
                        long total;
                        if (problemJidToTotalSubmissions.containsKey(problemJid)) {
                            total = problemJidToTotalSubmissions.get(problemJid);
                        } else {
                            total = 0;
                        }

                        total++;
                        problemJidToTotalSubmissions.put(problemJid, total);
                    }


                    Map<String, PointAndTime> userJidToPoints;
                    if (problemJidToMapUserJidToPoints.containsKey(problemJid)) {
                        userJidToPoints = problemJidToMapUserJidToPoints.get(problemJid);
                    } else {
                        userJidToPoints = Maps.newHashMap();
                    }

                    if (userJidToPoints.containsKey(userJid)) {
                        point = userJidToPoints.get(userJid).point;
                    } else {
                        point = -1;
                    }
                    if (bundleSubmission.getLatestScore() > point) {
                        userJidToPoints.put(userJid, new PointAndTime(bundleSubmission.getLatestScore(), bundleSubmission.getTime().getTime()));
                    }

                    problemJidToMapUserJidToPoints.put(problemJid, userJidToPoints);
                }

                List<ProgrammingSubmission> programmingSubmissions = programmingSubmissionService.getAllProgrammingSubmissions();
                for (ProgrammingSubmission programmingSubmission : programmingSubmissions) {
                    String userJid = programmingSubmission.getAuthorJid();
                    Map<String, Double> problemJidToPoints;
                    if (userJidToMapProblemJidToPoints.containsKey(userJid)) {
                        problemJidToPoints = userJidToMapProblemJidToPoints.get(userJid);
                    } else {
                        problemJidToPoints = Maps.newHashMap();
                    }

                    String problemJid = programmingSubmission.getProblemJid();
                    double point;
                    if (problemJidToPoints.containsKey(problemJid)) {
                        point = problemJidToPoints.get(problemJid);
                    } else {
                        point = -1;
                    }
                    if (programmingSubmission.getLatestScore() > point) {
                        problemJidToPoints.put(problemJid, (double) programmingSubmission.getLatestScore());
                    }

                    userJidToMapProblemJidToPoints.put(userJid, problemJidToPoints);

                    if ((timeNow - programmingSubmission.getTime().getTime()) <= TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS)) {
                        long total;
                        if (problemJidToTotalSubmissions.containsKey(problemJid)) {
                            total = problemJidToTotalSubmissions.get(problemJid);
                        } else {
                            total = 0;
                        }

                        total++;
                        problemJidToTotalSubmissions.put(problemJid, total);
                    }

                    Map<String, PointAndTime> userJidToPoints;
                    if (problemJidToMapUserJidToPoints.containsKey(problemJid)) {
                        userJidToPoints = problemJidToMapUserJidToPoints.get(problemJid);
                    } else {
                        userJidToPoints = Maps.newHashMap();
                    }

                    if (userJidToPoints.containsKey(userJid)) {
                        point = userJidToPoints.get(userJid).point;
                    } else {
                        point = -1;
                    }
                    if (programmingSubmission.getLatestScore() > point) {
                        userJidToPoints.put(userJid, new PointAndTime((double) programmingSubmission.getLatestScore(), programmingSubmission.getTime().getTime()));
                    }

                    problemJidToMapUserJidToPoints.put(problemJid, userJidToPoints);
                }

                List<PointStatisticEntry> pointStatisticEntries = Lists.newArrayList();
                for (String userJid : userJidToMapProblemJidToPoints.keySet()) {
                    Map<String, Double> problemJidToPoints = userJidToMapProblemJidToPoints.get(userJid);
                    double point = problemJidToPoints.values().stream().mapToDouble(Double::doubleValue).sum();
                    pointStatisticEntries.add(new PointStatisticEntry(userJid, point, problemJidToPoints.size()));
                }
                Collections.sort(pointStatisticEntries);

                List<ProblemStatisticEntry> problemStatisticEntries = Lists.newArrayList();
                for (String problemJid : problemJidToTotalSubmissions.keySet()) {
                    problemStatisticEntries.add(new ProblemStatisticEntry(problemJid, problemJidToTotalSubmissions.get(problemJid)));
                }
                Collections.sort(problemStatisticEntries);

                pointStatisticService.updatePointStatistic(pointStatisticEntries, timeNow);
                problemStatisticService.updateProblemStatistic(problemStatisticEntries, timeNow);

                for (String problemJid : problemJidToMapUserJidToPoints.keySet()) {
                    List<ProblemScoreStatisticEntry> problemScoreStatisticEntries = Lists.newArrayList();
                    Map<String, PointAndTime> userJidToPoints = problemJidToMapUserJidToPoints.get(problemJid);
                    for (String userJid : userJidToPoints.keySet()) {
                        PointAndTime pointAndTime = userJidToPoints.get(userJid);
                        problemScoreStatisticEntries.add(new ProblemScoreStatisticEntry(userJid, pointAndTime.point, pointAndTime.time));
                    }
                    Collections.sort(problemScoreStatisticEntries);
                    problemScoreStatisticService.updateProblemStatistic(problemScoreStatisticEntries, problemJid, timeNow);
                }
            });
    }

    private class PointAndTime {
        private final double point;
        private final long time;

        public PointAndTime(double point, long time) {
            this.point = point;
            this.time = time;
        }
    }
}
