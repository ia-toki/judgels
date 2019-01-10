package org.iatoki.judgels.sandalphon.client.lesson;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

import java.util.List;

@ImplementedBy(ClientLessonHibernateDao.class)
public interface ClientLessonDao extends Dao<Long, ClientLessonModel> {

    boolean existsByClientJidAndLessonJid(String clientJid, String lessonJid);

    ClientLessonModel findByClientJidAndLessonJid(String clientJid, String lessonJid);

    List<ClientLessonModel> getByLessonJid(String lessonJid);
}
