package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.model.LessonPartnerModel;
import judgels.persistence.model.LessonPartnerModel_;

public final class LessonPartnerDao extends Dao<LessonPartnerModel> {

    @Inject
    public LessonPartnerDao(DaoData data) {
        super(data);
    }

    public Optional<LessonPartnerModel> selectByLessonJidAndUserJid(String lessonJid, String userJid) {
        return select()
                .where(columnEq(LessonPartnerModel_.lessonJid, lessonJid))
                .where(columnEq(LessonPartnerModel_.userJid, userJid))
                .unique();
    }

    public List<LessonPartnerModel> selectAllByLessonJid(String lessonJid) {
        return select().where(columnEq(LessonPartnerModel_.lessonJid, lessonJid)).all();
    }
}
