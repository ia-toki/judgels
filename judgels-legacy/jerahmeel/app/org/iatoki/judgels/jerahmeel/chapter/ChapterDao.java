package org.iatoki.judgels.jerahmeel.chapter;

import com.google.inject.ImplementedBy;
import judgels.jerahmeel.persistence.ChapterModel;
import judgels.persistence.JudgelsDao;

@ImplementedBy(ChapterHibernateDao.class)
public interface ChapterDao extends JudgelsDao<ChapterModel> {

}
