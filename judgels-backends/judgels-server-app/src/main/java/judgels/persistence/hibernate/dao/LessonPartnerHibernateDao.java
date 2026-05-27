package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import judgels.persistence.dao.LessonPartnerDao;
import judgels.persistence.model.LessonPartnerModel;
import judgels.persistence.model.LessonPartnerModel_;

public final class LessonPartnerHibernateDao extends HibernateDao<LessonPartnerModel> implements LessonPartnerDao {

    @Inject
    public LessonPartnerHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<LessonPartnerModel> selectByLessonJidAndUserJid(String lessonJid, String userJid) {
        return select()
                .where(columnEq(LessonPartnerModel_.lessonJid, lessonJid))
                .where(columnEq(LessonPartnerModel_.userJid, userJid))
                .unique();
    }

    @Override
    public List<LessonPartnerModel> selectAllByLessonJid(String lessonJid) {
        return select().where(columnEq(LessonPartnerModel_.lessonJid, lessonJid)).all();
    }
}
