package org.iatoki.judgels.jerahmeel.curriculum;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;

import java.util.List;

@ImplementedBy(CurriculumServiceImpl.class)
public interface CurriculumService {

    List<Curriculum> getAllCurriculums();

    Page<Curriculum> getPageOfCurriculums(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    Curriculum findCurriculumById(long curriculumId) throws CurriculumNotFoundException;

    Curriculum createCurriculum(String name, String description, String userJid, String userIpAddress);

    void updateCurriculum(String curriculumJid, String name, String description, String userJid, String userIpAddress);
}
