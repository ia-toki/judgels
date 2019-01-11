package org.iatoki.judgels.jerahmeel.chapter.lesson;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;

@ImplementedBy(ChapterLessonServiceImpl.class)
public interface ChapterLessonService {

    boolean aliasExistsInChapter(String chapterJid, String alias);

    ChapterLesson findChapterLessonById(long chapterLessonId) throws ChapterLessonNotFoundException;

    Page<ChapterLesson> getPageOfChapterLessons(String chapterJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    Page<ChapterLessonWithProgress> getPageOfChapterLessonsWithProgress(String userJid, String chapterJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    void addChapterLesson(String chapterJid, String lessonJid, String lessonSecret, String alias, ChapterLessonStatus status, String userJid, String userIpAddress);

    void updateChapterLesson(long chapterLessonId, String alias, ChapterLessonStatus status, String userJid, String userIpAddress);

    void removeChapterLesson(long chapterLessonId);
}
