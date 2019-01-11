package org.iatoki.judgels.jerahmeel.chapter.lesson;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.iatoki.judgels.jerahmeel.user.item.UserItemDao;
import org.iatoki.judgels.play.Page;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public final class ChapterLessonServiceImpl implements ChapterLessonService {

    private final ChapterLessonDao chapterLessonDao;
    private final UserItemDao userItemDao;

    @Inject
    public ChapterLessonServiceImpl(ChapterLessonDao chapterLessonDao, UserItemDao userItemDao) {
        this.chapterLessonDao = chapterLessonDao;
        this.userItemDao = userItemDao;
    }

    @Override
    public boolean aliasExistsInChapter(String chapterJid, String alias) {
        return chapterLessonDao.existsByChapterJidAndAlias(chapterJid, alias);
    }

    @Override
    public ChapterLesson findChapterLessonById(long chapterLessonId) throws ChapterLessonNotFoundException {
        ChapterLessonModel chapterLessonModel = chapterLessonDao.findById(chapterLessonId);
        if (chapterLessonModel != null) {
            return ChapterLessonServiceUtils.createFromModel(chapterLessonModel);
        } else {
            throw new ChapterLessonNotFoundException("Chapter Lesson Not Found");
        }
    }

    @Override
    public Page<ChapterLesson> getPageOfChapterLessons(String chapterJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = chapterLessonDao.countByFiltersEq(filterString, ImmutableMap.of(ChapterLessonModel_.chapterJid, chapterJid));
        List<ChapterLessonModel> chapterLessonModels = chapterLessonDao.findSortedByFiltersEq(orderBy, orderDir, filterString, ImmutableMap.of(ChapterLessonModel_.chapterJid, chapterJid), pageIndex * pageSize, pageSize);

        List<ChapterLesson> chapterLessons = chapterLessonModels.stream().map(m -> ChapterLessonServiceUtils.createFromModel(m)).collect(Collectors.toList());

        return new Page<>(chapterLessons, totalPages, pageIndex, pageSize);
    }

    @Override
    public Page<ChapterLessonWithProgress> getPageOfChapterLessonsWithProgress(String userJid, String chapterJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = chapterLessonDao.countByFiltersEq(filterString, ImmutableMap.of(ChapterLessonModel_.chapterJid, chapterJid, ChapterLessonModel_.status, ChapterLessonStatus.VISIBLE.name()));
        List<ChapterLessonModel> chapterLessonModels = chapterLessonDao.findSortedByFiltersEq(orderBy, orderDir, filterString, ImmutableMap.of(ChapterLessonModel_.chapterJid, chapterJid, ChapterLessonModel_.status, ChapterLessonStatus.VISIBLE.name()), pageIndex * pageSize, pageSize);

        ImmutableList.Builder<ChapterLessonWithProgress> chapterLessonProgressBuilder = ImmutableList.builder();
        for (ChapterLessonModel chapterLessonModel : chapterLessonModels) {
            LessonProgress progress = LessonProgress.NOT_VIEWED;
            if (userItemDao.existsByUserJidAndItemJid(userJid, chapterLessonModel.lessonJid)) {
                progress = LessonProgress.VIEWED;
            }
            chapterLessonProgressBuilder.add(new ChapterLessonWithProgress(ChapterLessonServiceUtils.createFromModel(chapterLessonModel), progress));
        }

        return new Page<>(chapterLessonProgressBuilder.build(), totalPages, pageIndex, pageSize);
    }

    @Override
    public void addChapterLesson(String chapterJid, String lessonJid, String lessonSecret, String alias, ChapterLessonStatus status, String userJid, String userIpAddress) {
        ChapterLessonModel chapterLessonModel = new ChapterLessonModel();
        chapterLessonModel.chapterJid = chapterJid;
        chapterLessonModel.lessonJid = lessonJid;
        chapterLessonModel.lessonSecret = lessonSecret;
        chapterLessonModel.alias = alias;
        chapterLessonModel.status = status.name();

        chapterLessonDao.persist(chapterLessonModel, userJid, userIpAddress);
    }

    @Override
    public void updateChapterLesson(long chapterLessonId, String alias, ChapterLessonStatus status, String userJid, String userIpAddress) {
        ChapterLessonModel chapterLessonModel = chapterLessonDao.findById(chapterLessonId);
        chapterLessonModel.alias = alias;
        chapterLessonModel.status = status.name();

        chapterLessonDao.edit(chapterLessonModel, userJid, userIpAddress);
    }

    @Override
    public void removeChapterLesson(long chapterLessonId) {
        ChapterLessonModel chapterLessonModel = chapterLessonDao.findById(chapterLessonId);

        chapterLessonDao.remove(chapterLessonModel);
    }
}
