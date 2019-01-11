package org.iatoki.judgels.jerahmeel.curriculum.course;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;

@ImplementedBy(CurriculumCourseServiceImpl.class)
public interface CurriculumCourseService {

    boolean existsByCurriculumJidAndAlias(String curriculumJid, String alias);

    boolean existsByCurriculumJidAndCourseJid(String curriculumJid, String courseJid);

    CurriculumCourse findCurriculumCourseByCurriculumCourseId(long curriculumCourseId) throws CurriculumCourseNotFoundException;

    Page<CurriculumCourse> getPageOfCurriculumCourses(String curriculumJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    Page<CurriculumCourseWithProgress> getPageOfCurriculumCoursesWithProgress(String userJid, String curriculumJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    CurriculumCourse addCurriculumCourse(String curriculumJid, String courseJid, String alias, String userJid, String userIpAddress);

    void updateCurriculumCourse(long curriculumCourseId, String alias, String userJid, String userIpAddress);

    void removeCurriculumCourse(long curriculumCourseId);
}
