package org.iatoki.judgels.jerahmeel.course.chapter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.iatoki.judgels.jerahmeel.chapter.ChapterScoreCacheUtils;
import org.iatoki.judgels.jerahmeel.user.item.UserItemStatus;
import org.iatoki.judgels.jerahmeel.chapter.dependency.ChapterDependencyDao;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemDao;
import org.iatoki.judgels.jerahmeel.user.item.UserItemDao;
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
public final class CourseChapterServiceImpl implements CourseChapterService {

    private final CourseChapterDao courseChapterDao;
    private final ChapterProblemDao chapterProblemDao;
    private final UserItemDao userItemDao;

    @Inject
    public CourseChapterServiceImpl(CourseChapterDao courseChapterDao, ChapterProblemDao chapterProblemDao, UserItemDao userItemDao) {
        this.courseChapterDao = courseChapterDao;
        this.chapterProblemDao = chapterProblemDao;
        this.userItemDao = userItemDao;
    }

    @Override
    public boolean existsByCourseJidAndAlias(String courseJid, String alias) {
        return courseChapterDao.existsByCourseJidAndAlias(courseJid, alias);
    }

    @Override
    public boolean existsByCourseJidAndChapterJid(String courseJid, String chapterJid) {
        return courseChapterDao.existsByCourseJidAndChapterJid(courseJid, chapterJid);
    }

    @Override
    public CourseChapter findCourseChapterById(long courseChapterId) throws CourseChapterNotFoundException {
        CourseChapterModel courseChapterModel = courseChapterDao.findById(courseChapterId);
        if (courseChapterModel != null) {
            return CourseChapterServiceUtils.createFromModel(courseChapterModel);
        } else {
            throw new CourseChapterNotFoundException("Course Chapter Not Found.");
        }
    }

    @Override
    public Page<CourseChapter> getPageOfCourseChapters(String courseJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = courseChapterDao.countByFiltersEq(filterString, ImmutableMap.of(CourseChapterModel_.courseJid, courseJid));
        List<CourseChapterModel> courseChapterModels = courseChapterDao.findSortedByFiltersEq(orderBy, orderDir, filterString, ImmutableMap.of(CourseChapterModel_.courseJid, courseJid), pageIndex * pageSize, pageSize);

        List<CourseChapter> courseChapters = courseChapterModels.stream().map(m -> CourseChapterServiceUtils.createFromModel(m)).collect(Collectors.toList());

        return new Page<>(courseChapters, totalPages, pageIndex, pageSize);
    }

    @Override
    public Page<CourseChapterWithProgress> getPageOfCourseChaptersWithProgress(String userJid, String courseJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = courseChapterDao.countByFiltersEq(filterString, ImmutableMap.of(CourseChapterModel_.courseJid, courseJid));
        List<CourseChapterModel> courseChapterModels = courseChapterDao.findSortedByFiltersEq(orderBy, orderDir, filterString, ImmutableMap.of(CourseChapterModel_.courseJid, courseJid), pageIndex * pageSize, pageSize);

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

        ImmutableList.Builder<CourseChapterWithProgress> courseChapterProgressBuilder = ImmutableList.builder();
        List<UserItemModel> completedUserItemModel = userItemDao.getByUserJidAndStatus(userJid, UserItemStatus.COMPLETED.name());
        Set<String> completedJids = completedUserItemModel.stream().map(m -> m.itemJid).collect(Collectors.toSet());
        List<UserItemModel> onProgressUserItemModel = userItemDao.getByUserJidAndStatus(userJid, UserItemStatus.VIEWED.name());
        Set<String> onProgressJids = onProgressUserItemModel.stream().map(m -> m.itemJid).collect(Collectors.toSet());

        for (CourseChapterModel courseChapterModel : courseChapterModels) {
            List<ChapterProblemModel> currentChapterProblemModels = mapChapterJidToChapterProblemModels.get(courseChapterModel.chapterJid);

            if (currentChapterProblemModels == null) {
                currentChapterProblemModels = ImmutableList.of();
            }

            long totalProblems = currentChapterProblemModels.size();
            long solvedProblems = 0;
            double totalScore = ChapterScoreCacheUtils.getInstance().getUserTotalScoreFromChapterProblemModels(userJid, courseChapterModel.chapterJid, currentChapterProblemModels);
            for (ChapterProblemModel chapterProblemModel : currentChapterProblemModels) {
                if (userItemDao.existsByUserJidItemJidAndStatus(userJid, chapterProblemModel.problemJid, UserItemStatus.COMPLETED.name())) {
                    solvedProblems++;
                }
            }

            ChapterProgress progress = ChapterProgress.AVAILABLE;
            if (completedJids.contains(courseChapterModel.chapterJid)) {
                progress = ChapterProgress.COMPLETED;
            } else if (onProgressJids.contains(courseChapterModel.chapterJid)) {
                progress = ChapterProgress.IN_PROGRESS;
            }
            courseChapterProgressBuilder.add(new CourseChapterWithProgress(CourseChapterServiceUtils.createFromModel(courseChapterModel), progress, solvedProblems, totalProblems, totalScore));
        }

        return new Page<>(courseChapterProgressBuilder.build(), totalPages, pageIndex, pageSize);
    }

    @Override
    public CourseChapter addCourseChapter(String courseJid, String chapterJid, String alias, String userJid, String userIpAddress) {
        CourseChapterModel courseChapterModel = new CourseChapterModel();
        courseChapterModel.courseJid = courseJid;
        courseChapterModel.chapterJid = chapterJid;
        courseChapterModel.alias = alias;

        courseChapterDao.persist(courseChapterModel, userJid, userIpAddress);

        return CourseChapterServiceUtils.createFromModel(courseChapterModel);
    }

    @Override
    public void updateCourseChapter(long courseChapterId, String alias, String userJid, String userIpAddress) {
        CourseChapterModel courseChapterModel = courseChapterDao.findById(courseChapterId);
        courseChapterModel.alias = alias;

        courseChapterDao.edit(courseChapterModel, userJid, userIpAddress);
    }

    @Override
    public void removeCourseChapter(long courseChapterId) {
        CourseChapterModel courseChapterModel = courseChapterDao.findById(courseChapterId);

        courseChapterDao.remove(courseChapterModel);
    }
}
