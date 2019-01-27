package org.iatoki.judgels.sandalphon.lesson.partner;

import com.google.inject.ImplementedBy;
import judgels.persistence.Dao;

import java.util.List;

@ImplementedBy(LessonPartnerHibernateDao.class)
public interface LessonPartnerDao extends Dao<LessonPartnerModel> {

    boolean existsByLessonJidAndPartnerJid(String lessonJid, String partnerJid);

    LessonPartnerModel findByLessonJidAndPartnerJid(String lessonJid, String partnerJid);

    List<String> getLessonJidsByPartnerJid(String partnerJid);
}
