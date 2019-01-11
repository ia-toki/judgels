package org.iatoki.judgels.jerahmeel.curriculum.course;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.iatoki.judgels.jerahmeel.chapter.ChapterScoreCacheUtils;
import org.iatoki.judgels.jerahmeel.user.item.UserItemStatus;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterDao;
import org.iatoki.judgels.jerahmeel.chapter.dependency.ChapterDependencyDao;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemDao;
import org.iatoki.judgels.jerahmeel.user.item.UserItemDao;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterModel;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterModel_;
import org.iatoki.judgels.jerahmeel.chapter.dependency.ChapterDependencyModel;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemModel;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemModel_;
import org.iatoki.judgels.jerahmeel.user.item.UserItemModel;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public final class CurriculumCourseServiceImpl implements CurriculumCourseService {

    private final CourseChapterDao courseChapterDao;
    private final CurriculumCourseDao curriculumCourseDao;
    private final ChapterProblemDao chapterProblemDao;
    private final UserItemDao userItemDao;

    @Inject
    public CurriculumCourseServiceImpl(CourseChapterDao courseChapterDao, CurriculumCourseDao curriculumCourseDao, ChapterProblemDao chapterProblemDao, UserItemDao userItemDao) {
        this.courseChapterDao = courseChapterDao;
        this.curriculumCourseDao = curriculumCourseDao;
        this.chapterProblemDao = chapterProblemDao;
        this.userItemDao = userItemDao;
    }

    @Override
    public boolean existsByCurriculumJidAndAlias(String curriculumJid, String alias) {
        return curriculumCourseDao.existsByCurriculumJidAndAlias(curriculumJid, alias);
    }

    @Override
    public boolean existsByCurriculumJidAndCourseJid(String curriculumJid, String courseJid) {
        return curriculumCourseDao.existsByCurriculumJidAndCourseJid(curriculumJid, courseJid);
    }

    @Override
    public CurriculumCourse findCurriculumCourseByCurriculumCourseId(long curriculumCourseId) throws CurriculumCourseNotFoundException {
        CurriculumCourseModel curriculumCourseModel = curriculumCourseDao.findById(curriculumCourseId);
        if (curriculumCourseModel != null) {
            return CurriculumCourseServiceUtils.createFromModel(curriculumCourseModel);
        } else {
            throw new CurriculumCourseNotFoundException("Curriculum Course Not Found.");
        }
    }

    @Override
    public Page<CurriculumCourse> getPageOfCurriculumCourses(String curriculumJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = curriculumCourseDao.countByFiltersEq(filterString, ImmutableMap.of(CurriculumCourseModel_.curriculumJid, curriculumJid));
        List<CurriculumCourseModel> curriculumCourseModels = curriculumCourseDao.findSortedByFiltersEq(orderBy, orderDir, filterString, ImmutableMap.of(CurriculumCourseModel_.curriculumJid, curriculumJid), pageIndex * pageSize, pageSize);

        List<CurriculumCourse> curriculumCourses = curriculumCourseModels.stream().map(m -> CurriculumCourseServiceUtils.createFromModel(m)).collect(Collectors.toList());

        return new Page<>(curriculumCourses, totalPages, pageIndex, pageSize);
    }

    @Override
    public Page<CurriculumCourseWithProgress> getPageOfCurriculumCoursesWithProgress(String userJid, String curriculumJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = curriculumCourseDao.countByFiltersEq(filterString, ImmutableMap.of(CurriculumCourseModel_.curriculumJid, curriculumJid));
        List<CurriculumCourseModel> curriculumCourseModels = curriculumCourseDao.findSortedByFiltersEq(orderBy, orderDir, filterString, ImmutableMap.of(CurriculumCourseModel_.curriculumJid, curriculumJid), pageIndex * pageSize, pageSize);

        List<String> courseJids = curriculumCourseModels.stream().map(m -> m.courseJid).collect(Collectors.toList());
        List<CourseChapterModel> courseChapterModels = courseChapterDao.findSortedByFiltersIn(orderBy, orderDir, "", ImmutableMap.of(CourseChapterModel_.courseJid, courseJids), 0, -1);
        Map<String, List<CourseChapterModel>> mapCourseJidToCourseChapterModels = Maps.newHashMap();
        for (CourseChapterModel courseChapterModel : courseChapterModels) {
            List<CourseChapterModel> value;
            if (mapCourseJidToCourseChapterModels.containsKey(courseChapterModel.courseJid)) {
                value = mapCourseJidToCourseChapterModels.get(courseChapterModel.courseJid);
            } else {
                value = Lists.newArrayList();
            }
            value.add(courseChapterModel);
            mapCourseJidToCourseChapterModels.put(courseChapterModel.courseJid, value);
        }

        List<String> chapterJids = courseChapterModels.stream().map(m -> m.chapterJid).collect(Collectors.toList());
        List<ChapterProblemModel> chapterProblemModels = chapterProblemDao.findSortedByFiltersIn(orderBy, orderDir, "", ImmutableMap.of(ChapterProblemModel_.chapterJid, chapterJids), 0, -1);
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

        ImmutableList.Builder<CurriculumCourseWithProgress> curriculumCourseProgressBuilder = ImmutableList.builder();
        for (CurriculumCourseModel curriculumCourseModel : curriculumCourseModels) {
            List<CourseChapterModel> currentCourseChapterModels = mapCourseJidToCourseChapterModels.get(curriculumCourseModel.courseJid);

            if (currentCourseChapterModels == null) {
                currentCourseChapterModels = ImmutableList.of();
            }

            CourseProgressWithCompleted courseProgressWithCompleted = getUserProgressFromCourseChapterModels(userJid, currentCourseChapterModels);

            double totalScore = ChapterScoreCacheUtils.getInstance().getUserTotalScoreFromCourseChapterModels(userJid, curriculumCourseModel.courseJid, currentCourseChapterModels, mapChapterJidToChapterProblemModels);

            curriculumCourseProgressBuilder.add(new CurriculumCourseWithProgress(CurriculumCourseServiceUtils.createFromModel(curriculumCourseModel), courseProgressWithCompleted.courseProgress, courseProgressWithCompleted.completed, currentCourseChapterModels.size(), totalScore));
        }

        return new Page<>(curriculumCourseProgressBuilder.build(), totalPages, pageIndex, pageSize);
    }

    @Override
    public CurriculumCourse addCurriculumCourse(String curriculumJid, String courseJid, String alias, String userJid, String userIpAddress) {
        CurriculumCourseModel curriculumCourseModel = new CurriculumCourseModel();
        curriculumCourseModel.curriculumJid = curriculumJid;
        curriculumCourseModel.courseJid = courseJid;
        curriculumCourseModel.alias = alias;

        curriculumCourseDao.persist(curriculumCourseModel, userJid, userIpAddress);

        return CurriculumCourseServiceUtils.createFromModel(curriculumCourseModel);
    }

    @Override
    public void updateCurriculumCourse(long curriculumCourseId, String alias, String userJid, String userIpAddress) {
        CurriculumCourseModel curriculumCourseModel = curriculumCourseDao.findById(curriculumCourseId);
        curriculumCourseModel.alias = alias;

        curriculumCourseDao.edit(curriculumCourseModel, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
    }

    @Override
    public void removeCurriculumCourse(long curriculumCourseId) {
        CurriculumCourseModel curriculumCourseModel = curriculumCourseDao.findById(curriculumCourseId);

        curriculumCourseDao.remove(curriculumCourseModel);
    }

    private CourseProgressWithCompleted getUserProgressFromCourseChapterModels(String userJid, List<CourseChapterModel> courseChapterModels) {
        List<UserItemModel> completedUserItemModel = userItemDao.getByUserJidAndStatus(userJid, UserItemStatus.COMPLETED.name());
        Set<String> completedJids = completedUserItemModel.stream().map(m -> m.itemJid).collect(Collectors.toSet());
        List<UserItemModel> onProgressUserItemModel = userItemDao.getByUserJidAndStatus(userJid, UserItemStatus.VIEWED.name());
        Set<String> onProgressJids = onProgressUserItemModel.stream().map(m -> m.itemJid).collect(Collectors.toSet());

        int completed = 0;
        CourseProgress progress = CourseProgress.AVAILABLE;
        for (CourseChapterModel courseChapterModel : courseChapterModels) {
            if (completedJids.contains(courseChapterModel.chapterJid)) {
                progress = CourseProgress.IN_PROGRESS;
                completed++;
            } else if (onProgressJids.contains(courseChapterModel.chapterJid)) {
                progress = CourseProgress.IN_PROGRESS;
                break;
            }
        }
        if (completed == courseChapterModels.size()) {
            progress = CourseProgress.COMPLETED;
        }

        return new CourseProgressWithCompleted(completed, progress);
    }

    private class CourseProgressWithCompleted {
        private final long completed;
        private final CourseProgress courseProgress;

        public CourseProgressWithCompleted(long completed, CourseProgress courseProgress) {
            this.completed = completed;
            this.courseProgress = courseProgress;
        }
    }
}
