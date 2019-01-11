package org.iatoki.judgels.jerahmeel.course.chapter;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;

@ImplementedBy(CourseChapterServiceImpl.class)
public interface CourseChapterService {

    boolean existsByCourseJidAndAlias(String courseJid, String alias);

    boolean existsByCourseJidAndChapterJid(String courseJid, String chapterJid);

    CourseChapter findCourseChapterById(long courseChapterId) throws CourseChapterNotFoundException;

    Page<CourseChapter> getPageOfCourseChapters(String courseJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    Page<CourseChapterWithProgress> getPageOfCourseChaptersWithProgress(String userJid, String courseJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    CourseChapter addCourseChapter(String courseJid, String chapterJid, String alias, String userJid, String userIpAddress);

    void updateCourseChapter(long courseChapterId, String alias, String userJid, String userIpAddress);

    void removeCourseChapter(long courseChapterId);
}
