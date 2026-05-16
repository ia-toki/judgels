package judgels.hibernate;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import judgels.persistence.LessonPartnerDao;
import judgels.persistence.LessonPartnerModel;
import judgels.persistence.LessonPartnerModel_;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

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
