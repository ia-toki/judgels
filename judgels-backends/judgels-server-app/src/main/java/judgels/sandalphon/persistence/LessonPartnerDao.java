package judgels.sandalphon.persistence;

import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;

public interface LessonPartnerDao extends Dao<LessonPartnerModel> {
    Optional<LessonPartnerModel> selectByLessonJidAndUserJid(String lessonJid, String userJid);
    List<LessonPartnerModel> selectAllByLessonJid(String lessonJid);
}
