package org.iatoki.judgels.jerahmeel.chapter;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.JudgelsDao;

@ImplementedBy(ChapterHibernateDao.class)
public interface ChapterDao extends JudgelsDao<ChapterModel> {

}
