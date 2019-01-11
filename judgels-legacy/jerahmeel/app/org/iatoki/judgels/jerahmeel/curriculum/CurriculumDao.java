package org.iatoki.judgels.jerahmeel.curriculum;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.JudgelsDao;

@ImplementedBy(CurriculumHibernateDao.class)
public interface CurriculumDao extends JudgelsDao<CurriculumModel> {

}
