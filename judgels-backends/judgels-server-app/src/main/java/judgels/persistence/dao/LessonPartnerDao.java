package judgels.persistence.dao;

import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.model.LessonPartnerModel;

public interface LessonPartnerDao extends Dao<LessonPartnerModel> {
    Optional<LessonPartnerModel> selectByLessonJidAndUserJid(String lessonJid, String userJid);
    List<LessonPartnerModel> selectAllByLessonJid(String lessonJid);
}
