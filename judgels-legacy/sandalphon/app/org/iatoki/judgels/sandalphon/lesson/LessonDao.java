package org.iatoki.judgels.sandalphon.lesson;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.JudgelsDao;

import java.util.List;

@ImplementedBy(LessonHibernateDao.class)
public interface LessonDao extends JudgelsDao<LessonModel> {

    List<String> getJidsByAuthorJid(String authorJid);

    boolean existsBySlug(String slug);
}
