package org.iatoki.judgels.sandalphon.lesson;

import com.google.inject.ImplementedBy;
import java.util.List;
import judgels.persistence.JudgelsDao;

@ImplementedBy(LessonHibernateDao.class)
public interface LessonDao extends JudgelsDao<LessonModel> {

    List<String> getJidsByAuthorJid(String authorJid);

    LessonModel findBySlug(String slug);

    boolean existsBySlug(String slug);
}
