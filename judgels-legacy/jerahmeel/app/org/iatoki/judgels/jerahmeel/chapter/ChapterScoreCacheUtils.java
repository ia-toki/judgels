package org.iatoki.judgels.jerahmeel.chapter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.iatoki.judgels.jerahmeel.ProblemScore;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemDao;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemModel;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemModel_;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterDao;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterModel;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterModel_;
import org.iatoki.judgels.jerahmeel.grading.bundle.BundleGradingDao;
import org.iatoki.judgels.jerahmeel.grading.bundle.BundleGradingModel;
import org.iatoki.judgels.jerahmeel.grading.programming.ProgrammingGradingDao;
import org.iatoki.judgels.jerahmeel.grading.programming.ProgrammingGradingModel;
import org.iatoki.judgels.jerahmeel.scorecache.ContainerProblemScoreCacheDao;
import org.iatoki.judgels.jerahmeel.scorecache.ContainerProblemScoreCacheModel;
import org.iatoki.judgels.jerahmeel.scorecache.ContainerProblemScoreCacheServiceUtils;
import org.iatoki.judgels.jerahmeel.scorecache.ContainerScoreCacheDao;
import org.iatoki.judgels.jerahmeel.scorecache.ContainerScoreCacheModel;
import org.iatoki.judgels.jerahmeel.scorecache.ContainerScoreCacheServiceUtils;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionDao;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionModel;
import org.iatoki.judgels.jerahmeel.submission.programming.ProgrammingSubmissionDao;
import org.iatoki.judgels.jerahmeel.submission.programming.ProgrammingSubmissionModel;
import org.iatoki.judgels.sandalphon.problem.base.ProblemType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class ChapterScoreCacheUtils {

    private static ChapterScoreCacheUtils instance;

    private final BundleSubmissionDao bundleSubmissionDao;
    private final BundleGradingDao bundleGradingDao;
    private final ContainerScoreCacheDao containerScoreCacheDao;
    private final ContainerProblemScoreCacheDao containerProblemScoreCacheDao;
    private final CourseChapterDao courseChapterDao;
    private final ProgrammingSubmissionDao programmingSubmissionDao;
    private final ProgrammingGradingDao programmingGradingDao;
    private final ChapterProblemDao chapterProblemDao;

    public ChapterScoreCacheUtils(BundleSubmissionDao bundleSubmissionDao, BundleGradingDao bundleGradingDao, ContainerScoreCacheDao containerScoreCacheDao, ContainerProblemScoreCacheDao containerProblemScoreCacheDao, CourseChapterDao courseChapterDao, ProgrammingSubmissionDao programmingSubmissionDao, ProgrammingGradingDao programmingGradingDao, ChapterProblemDao chapterProblemDao) {
        this.bundleSubmissionDao = bundleSubmissionDao;
        this.bundleGradingDao = bundleGradingDao;
        this.containerScoreCacheDao = containerScoreCacheDao;
        this.containerProblemScoreCacheDao = containerProblemScoreCacheDao;
        this.courseChapterDao = courseChapterDao;
        this.programmingSubmissionDao = programmingSubmissionDao;
        this.programmingGradingDao = programmingGradingDao;
        this.chapterProblemDao = chapterProblemDao;
    }

    public static synchronized void buildInstance(BundleSubmissionDao bundleSubmissionDao, BundleGradingDao bundleGradingDao, ContainerScoreCacheDao containerScoreCacheDao, ContainerProblemScoreCacheDao containerProblemScoreCacheDao, CourseChapterDao courseChapterDao, ProgrammingSubmissionDao programmingSubmissionDao, ProgrammingGradingDao programmingGradingDao, ChapterProblemDao chapterProblemDao) {
        if (instance != null) {
            throw new UnsupportedOperationException("ChapterScoreCacheUtils instance has already been built");
        }
        instance = new ChapterScoreCacheUtils(bundleSubmissionDao, bundleGradingDao, containerScoreCacheDao, containerProblemScoreCacheDao, courseChapterDao, programmingSubmissionDao, programmingGradingDao, chapterProblemDao);
    }

    public static ChapterScoreCacheUtils getInstance() {
        if (instance == null) {
            throw new UnsupportedOperationException("ChapterScoreCacheUtils instance has not been built");
        }
        return instance;
    }

    public double getUserTotalScoreFromCourseChapterModels(String userJid, String courseJid, List<CourseChapterModel> courseChapterModels, Map<String, List<ChapterProblemModel>> mapChapterJidToChapterProblemModels) {
        if (containerScoreCacheDao.existsByUserJidAndContainerJid(userJid, courseJid)) {
            ContainerScoreCacheModel containerScoreCacheModel = containerScoreCacheDao.getByUserJidAndContainerJid(userJid, courseJid);
            return containerScoreCacheModel.score;
        }

        double totalScore = getUserTotalScoreFromCourseChapterModelsWithoutCache(userJid, courseChapterModels, mapChapterJidToChapterProblemModels);

        ContainerScoreCacheServiceUtils.addToContainerScoreCache(containerScoreCacheDao, userJid, courseJid, totalScore);
        return totalScore;
    }

    public double getUserTotalScoreFromCourseChapterModelsWithoutCache(String userJid, List<CourseChapterModel> courseChapterModels, Map<String, List<ChapterProblemModel>> mapChapterJidToChapterProblemModels) {
        double totalScore = 0;
        for (CourseChapterModel courseChapterModel : courseChapterModels) {
            List<ChapterProblemModel> chapterProblemModels = mapChapterJidToChapterProblemModels.get(courseChapterModel.chapterJid);
            if (chapterProblemModels == null) {
                chapterProblemModels = ImmutableList.of();
            }

            double chapterScore = getUserTotalScoreFromChapterProblemModels(userJid, courseChapterModel.chapterJid, chapterProblemModels);

            totalScore += chapterScore;
        }

        return totalScore;
    }

    public double getUserTotalScoreFromChapterProblemModels(String userJid, String chapterJid, List<ChapterProblemModel> chapterProblemModels) {
        if (containerScoreCacheDao.existsByUserJidAndContainerJid(userJid, chapterJid)) {
            ContainerScoreCacheModel containerScoreCacheModel = containerScoreCacheDao.getByUserJidAndContainerJid(userJid, chapterJid);
            return containerScoreCacheModel.score;
        }

        double totalScore = getUserTotalScoreFromChapterProblemModelsWithoutCache(userJid, chapterProblemModels);

        ContainerScoreCacheServiceUtils.addToContainerScoreCache(containerScoreCacheDao, userJid, chapterJid, totalScore);
        return totalScore;
    }

    public double getUserTotalScoreFromChapterProblemModelsWithoutCache(String userJid, List<ChapterProblemModel> chapterProblemModels) {
        double totalScore = 0;
        for (ChapterProblemModel chapterProblemModel : chapterProblemModels) {
            double chapterProblemScore = getUserMaxScoreFromChapterProblemModel(userJid, chapterProblemModel);
            if (Double.compare(ProblemScore.MINIMUM_SCORE, chapterProblemScore) != 0) {
                totalScore += chapterProblemScore;
            }
        }

        return totalScore;
    }

    public double getUserMaxScoreFromChapterProblemModel(String userJid, ChapterProblemModel chapterProblemModel) {
        if (containerProblemScoreCacheDao.existsByUserJidContainerJidAndProblemJid(userJid, chapterProblemModel.chapterJid, chapterProblemModel.problemJid)) {
            ContainerProblemScoreCacheModel containerProblemScoreCacheModel = containerProblemScoreCacheDao.getByUserJidContainerJidAndProblemJid(userJid, chapterProblemModel.chapterJid, chapterProblemModel.problemJid);
            return containerProblemScoreCacheModel.score;
        }

        double maxScore = ProblemScore.MINIMUM_SCORE;
        if (chapterProblemModel.type.equals(ProblemType.BUNDLE.name())) {
            List<BundleSubmissionModel> bundleSubmissionModels = bundleSubmissionDao.getByContainerJidAndUserJidAndProblemJid(chapterProblemModel.chapterJid, userJid, chapterProblemModel.problemJid);

            Map<String, List<BundleGradingModel>> bundleGradingModels = bundleGradingDao.getBySubmissionJids(bundleSubmissionModels.stream().map(m -> m.jid).collect(Collectors.toList()));

            for (String submissionJid : bundleGradingModels.keySet()) {
                double submissionScore = bundleGradingModels.get(submissionJid).get(bundleGradingModels.get(submissionJid).size() - 1).score;
                if (submissionScore > maxScore) {
                    maxScore = submissionScore;
                }
            }
        } else if (chapterProblemModel.type.equals(ProblemType.PROGRAMMING.name())) {
            List<ProgrammingSubmissionModel> programmingSubmissionModels = programmingSubmissionDao.getByContainerJidAndUserJidAndProblemJid(chapterProblemModel.chapterJid, userJid, chapterProblemModel.problemJid);

            Map<String, List<ProgrammingGradingModel>> gradingModels = programmingGradingDao.getBySubmissionJids(programmingSubmissionModels.stream().map(m -> m.jid).collect(Collectors.toList()));

            for (String submissionJid : gradingModels.keySet()) {
                double submissionScore = gradingModels.get(submissionJid).get(gradingModels.get(submissionJid).size() - 1).score;
                if (submissionScore > maxScore) {
                    maxScore = submissionScore;
                }
            }
        }

        ContainerProblemScoreCacheServiceUtils.addToContainerProblemScoreCache(containerProblemScoreCacheDao, userJid, chapterProblemModel.chapterJid, chapterProblemModel.problemJid, maxScore);

        return maxScore;
    }

    public void updateChapterAndCourseScoreCache(String userJid, String chapterJid, double deltaScore) {
        if (containerScoreCacheDao.existsByUserJidAndContainerJid(userJid, chapterJid)) {
            ContainerScoreCacheModel containerScoreCacheModel = containerScoreCacheDao.getByUserJidAndContainerJid(userJid, chapterJid);
            containerScoreCacheModel.score = containerScoreCacheModel.score + deltaScore;

            containerScoreCacheDao.edit(containerScoreCacheModel, "cacheAfterGradeUpdater", "localhost");
        } else {
            List<ChapterProblemModel> chapterProblemModels = chapterProblemDao.getByChapterJid(chapterJid);
            double chapterScore = ChapterScoreCacheUtils.getInstance().getUserTotalScoreFromChapterProblemModelsWithoutCache(userJid, chapterProblemModels);

            ContainerScoreCacheServiceUtils.addToContainerScoreCache(containerScoreCacheDao, userJid, chapterJid, chapterScore);
        }

        List<String> courseJids = courseChapterDao.findSortedByFiltersEq("id", "asc", "", ImmutableMap.of(CourseChapterModel_.chapterJid, chapterJid), 0, -1).stream().map(c -> c.courseJid).collect(Collectors.toList());
        List<CourseChapterModel> courseChapterModels = courseChapterDao.findSortedByFiltersIn("id", "asc", "", ImmutableMap.of(CourseChapterModel_.courseJid, courseJids), 0, -1);
        Map<String, List<CourseChapterModel>> mapCourseJidToCourseChapterModels = Maps.newHashMap();
        ImmutableSet.Builder<String> chapterJidsSetBuilder = ImmutableSet.builder();
        for (CourseChapterModel courseChapterModel : courseChapterModels) {
            List<CourseChapterModel> value;
            if (mapCourseJidToCourseChapterModels.containsKey(courseChapterModel.courseJid)) {
                value = mapCourseJidToCourseChapterModels.get(courseChapterModel.courseJid);
            } else {
                value = Lists.newArrayList();
            }

            value.add(courseChapterModel);
            mapCourseJidToCourseChapterModels.put(courseChapterModel.courseJid, value);
            chapterJidsSetBuilder.add(courseChapterModel.chapterJid);
        }

        Set<String> chapterJidsSet = chapterJidsSetBuilder.build();
        List<ChapterProblemModel> chapterProblemModels = chapterProblemDao.findSortedByFiltersIn("id", "asc", "", ImmutableMap.of(ChapterProblemModel_.chapterJid, chapterJidsSet), 0, -1);
        Map<String, List<ChapterProblemModel>> mapChapterJidToChapterProblemModels = Maps.newHashMap();
        for (ChapterProblemModel chapterProblemModel : chapterProblemModels) {
            List<ChapterProblemModel> value;
            if (mapChapterJidToChapterProblemModels.containsKey(chapterProblemModel.chapterJid)) {
                value = mapChapterJidToChapterProblemModels.get(chapterProblemModel.chapterJid);
            } else {
                value = Lists.newArrayList();
            }

            value.add(chapterProblemModel);
            mapChapterJidToChapterProblemModels.put(chapterProblemModel.chapterJid, value);
        }

        for (String courseJid : mapCourseJidToCourseChapterModels.keySet()) {
            updateCourseScoreCache(userJid, courseJid, deltaScore, mapCourseJidToCourseChapterModels, mapChapterJidToChapterProblemModels);
        }
    }

    public void updateCourseScoreCache(String userJid, String courseJid, double deltaScore, Map<String, List<CourseChapterModel>> mapCourseJidToCourseChapterModels, Map<String, List<ChapterProblemModel>> mapChapterJidToChapterProblemModels) {
        if (containerScoreCacheDao.existsByUserJidAndContainerJid(userJid, courseJid)) {
            ContainerScoreCacheModel containerScoreCacheModel = containerScoreCacheDao.getByUserJidAndContainerJid(userJid, courseJid);
            containerScoreCacheModel.score = containerScoreCacheModel.score + deltaScore;

            containerScoreCacheDao.edit(containerScoreCacheModel, "cacheAfterGradeUpdater", "localhost");
        } else {
            double courseScore = ChapterScoreCacheUtils.getInstance().getUserTotalScoreFromCourseChapterModelsWithoutCache(userJid, mapCourseJidToCourseChapterModels.get(courseJid), mapChapterJidToChapterProblemModels);

            ContainerScoreCacheServiceUtils.addToContainerScoreCache(containerScoreCacheDao, userJid, courseJid, courseScore);
        }
    }
}
